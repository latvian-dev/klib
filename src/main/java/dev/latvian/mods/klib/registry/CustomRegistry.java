package dev.latvian.mods.klib.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.command.CustomRegistryArgument;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.io.IOUtils;
import dev.latvian.mods.klib.net.SyncCustomRegistryMetaPayload;
import dev.latvian.mods.klib.net.SyncCustomRegistryValuesPayload;
import dev.latvian.mods.klib.platform.PlatformHelper;
import dev.latvian.mods.klib.platform.PlatformType;
import dev.latvian.mods.klib.util.Cast;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomRegistry<B extends ByteBuf, V> implements Iterable<Ref<V>> {
	private static final Map<ResourceKey<? extends Registry<?>>, CustomRegistry<?, ?>> ALL0 = new Reference2ObjectLinkedOpenHashMap<>();
	public static final Map<ResourceKey<? extends Registry<?>>, CustomRegistry<?, ?>> ALL = Collections.unmodifiableMap(ALL0);
	private static final Map<DataType<?>, CustomRegistry<?, ?>> DATA_TYPE_TO_REGISTRY0 = new Reference2ObjectLinkedOpenHashMap<>();
	public static final Map<DataType<?>, CustomRegistry<?, ?>> DATA_TYPE_TO_REGISTRY = Collections.unmodifiableMap(DATA_TYPE_TO_REGISTRY0);

	public static void registerAll(Consumer<CustomRegistryCollector> callback) {
		ALL0.clear();
		DATA_TYPE_TO_REGISTRY0.clear();

		callback.accept(new CustomRegistryCollector() {
			@Override
			public <T> void register(CustomRegistry<?, T> registry) {
				ALL0.put(registry.registryKeys().root(), registry);
				DATA_TYPE_TO_REGISTRY0.put(registry.dataType, registry);
			}
		});
	}

	public static void buildAllMeta() {
		for (var registry : ALL.values()) {
			if (registry.syncValues()) {
				registry.buildMeta();
			}
		}
	}

	public static void sync(ServerPlayer player) {
		var registryAccess = player.registryAccess();
		var platformType = PlatformHelper.CURRENT.getPlatformOf(player);
		var packets = new ArrayList<Packet<? super ClientGamePacketListener>>();
		var metaList = new ArrayList<CustomRegistryMetaInfo<?>>();

		for (var registry : CustomRegistry.ALL.values()) {
			metaList.add(registry.writeMeta());
		}

		packets.add(new ClientboundCustomPayloadPacket(new SyncCustomRegistryMetaPayload(metaList)));

		for (var registry : CustomRegistry.ALL.values()) {
			packets.add(new ClientboundCustomPayloadPacket(new SyncCustomRegistryValuesPayload(registry.writeValues(registryAccess, platformType))));
		}

		if (!packets.isEmpty()) {
			player.connection.send(new ClientboundBundlePacket(packets));
		}
	}

	private record CustomRegistryTypeCollectorImpl<B extends ByteBuf, V>(CustomRegistry<B, V> registry, List<CustomRegistryType<B, V>> list) implements CustomRegistryTypeCollector<B, V> {
		@Override
		public void register(CustomRegistryType<B, V> type) {
			list.add(type);
		}

		@Override
		public void register(Identifier id, V unit) {
			register(registry.unit(id, unit));
		}
	}

	private record ValueStreamCodec<B extends ByteBuf, T>(CustomRegistry<B, T> registry) implements StreamCodec<B, Ref<T>> {
		@Override
		public Ref<T> decode(B buf) {
			return registry.decodeValue(buf);
		}

		@Override
		public void encode(B buf, Ref<T> value) {
			registry.encodeValue(buf, value);
		}
	}

	public static class Builder<B extends ByteBuf, T> {
		private RegistryKeys<T> registryKeys;
		private boolean syncValues = true;
		private CustomRegistryTypeProvider<B, T> typeProvider;
		private Codec<T> customCodec;

		public Builder<B, T> keys(RegistryKeys<T> registryKeys) {
			this.registryKeys = registryKeys;
			return this;
		}

		public Builder<B, T> keys(Identifier id, String commonNamespace) {
			return keys(RegistryKeys.createKeys(id, commonNamespace));
		}

		public Builder<B, T> noValueSync() {
			this.syncValues = false;
			return this;
		}

		public Builder<B, T> type(CustomRegistryTypeProvider<B, T> typeProvider) {
			this.typeProvider = typeProvider;
			return this;
		}

		public Builder<B, T> customCodec(Codec<T> customCodec) {
			this.customCodec = customCodec;
			return this;
		}

		public CustomRegistry<B, T> build() {
			return new CustomRegistry<>(
				syncValues,
				Objects.requireNonNull(registryKeys, "You must specify .keys()"),
				typeProvider,
				customCodec
			);
		}
	}

	public static <B extends ByteBuf, T> Builder<B, T> builder() {
		return new Builder<>();
	}

	private final boolean syncValues;
	private final RegistryKeys<V> registryKeys;
	private final CustomRegistryTypeProvider<B, V> typeProvider;

	private final Map<ResourceKey<V>, Ref.OfKey<V>> refMap;
	private final Map<ResourceKey<V>, CustomRegistryType<B, V>> typeMap;
	private List<CustomRegistryType<B, V>> typeList;
	private final Int2ObjectMap<CustomRegistryType<B, V>> rxTypeMap;
	private final Reference2IntMap<CustomRegistryType<B, V>> txTypeMap;

	public final Codec<CustomRegistryType<B, V>> typeCodec;
	private final Codec<V> directCodec;
	private final Codec<Ref<V>> codec;
	private final StreamCodec<B, Ref<V>> streamCodec;
	private final DataType<Ref<V>> dataType;

	private final Map<ResourceKey<V>, Ref<V>> valueMap;
	private List<Ref<V>> valueList;
	private final Int2ObjectMap<Ref<V>> rxValueMap;
	private final Reference2IntMap<ResourceKey<V>> txValueMap;

	private CustomRegistry(
		boolean syncValues,
		RegistryKeys<V> registryKeys,
		@Nullable CustomRegistryTypeProvider<B, V> typeProvider,
		@Nullable Codec<V> customCodec
	) {
		this.syncValues = syncValues;
		this.registryKeys = registryKeys;
		this.typeProvider = typeProvider;

		this.refMap = new Reference2ObjectLinkedOpenHashMap<>();
		this.typeMap = new Reference2ObjectLinkedOpenHashMap<>();
		this.typeList = List.of();
		this.rxTypeMap = new Int2ObjectOpenHashMap<>();
		this.txTypeMap = new Reference2IntOpenHashMap<>();

		this.typeCodec = KLibCodecs.map(typeMap, registryKeys.codec(), CustomRegistryType::key);

		Codec<Ref<V>> unitCodec = registryKeys.codec().flatXmap(key -> {
			var type = getType(key);

			if (type == null) {
				return DataResult.error(() -> "Value " + key + " not found");
			}

			var unit = type.unit();

			if (unit != null) {
				return DataResult.success(unit);
			} else {
				return DataResult.error(() -> "Type " + key + " is not a unit type");
			}
		}, ref -> {
			if (ref instanceof CustomRegistryType.Unit<?, V>) {
				return DataResult.success(ref.key());
			} else {
				return DataResult.error(() -> "Type is not a unit type");
			}
		});

		var directCodec = typeCodec.dispatch("type", this::getType, CustomRegistryType::codec);

		if (customCodec != null) {
			directCodec = KLibCodecs.or(customCodec, directCodec);
		}

		this.directCodec = directCodec;

		this.codec = KLibCodecs.or(unitCodec, directCodec.xmap(v -> {
			if (v instanceof RefOptimizer<?> o) {
				v = (V) o.optimize();
			}

			return ref(v);
		}, Ref::value));

		this.streamCodec = new ValueStreamCodec<>(this);
		this.dataType = DataType.of(codec, Cast.to(streamCodec));

		this.valueMap = new Reference2ObjectOpenHashMap<>();
		this.valueList = List.of();
		this.rxValueMap = new Int2ObjectOpenHashMap<>();
		this.txValueMap = new Reference2IntOpenHashMap<>();
	}

	public CustomRegistryType.Unit<B, V> unitWithType(Identifier id, Function<CustomRegistryType<B, V>, V> instance) {
		return new CustomRegistryType.Unit<>(registryKeys.create(id), instance);
	}

	public CustomRegistryType.Unit<B, V> unit(Identifier id, V instance) {
		return new CustomRegistryType.Unit<>(registryKeys.create(id), _ -> instance);
	}

	public <D extends V> CustomRegistryType.Dynamic<B, V> dynamic(Identifier id, MapCodec<D> codec, StreamCodec<? super B, D> streamCodec) {
		return new CustomRegistryType.Dynamic<>(registryKeys.create(id), Cast.to(codec), Cast.to(streamCodec));
	}

	public Ref<V> ref(Identifier id) {
		return ref(registryKeys.create(id));
	}

	public Ref<V> ref(ResourceKey<V> key) {
		var type = getType(key);

		if (type instanceof CustomRegistryType.Unit<?, V> unit) {
			return unit;
		}

		return refMap.computeIfAbsent(key, Ref.OfKey::new);
	}

	Ref<V> createRef(ResourceKey<V> key, V value) {
		var ref = refMap.computeIfAbsent(key, Ref.OfKey::new);
		ref.value = value;
		return ref;
	}

	public Ref<V> ref(V value) {
		for (var ref : valueList) {
			if (ref.optionalValue() == value) {
				return ref;
			}
		}

		return new Ref.OfValue<>(value);
	}

	public void registerTypes(Consumer<CustomRegistryTypeCollector<B, V>> callback) {
		typeMap.clear();
		rxTypeMap.clear();
		txTypeMap.clear();
		typeList = new ArrayList<>();
		callback.accept(new CustomRegistryTypeCollectorImpl<>(this, typeList));
		typeList.sort(null);

		for (var type : typeList) {
			typeMap.put(type.key(), type);
		}

		typeList = List.copyOf(typeMap.values());
		updateValues(Map.of());
	}

	private void buildMeta() {
		rxTypeMap.clear();
		txTypeMap.clear();

		int index = 2;

		for (var type : typeList) {
			rxTypeMap.put(index, type);
			txTypeMap.put(type, index);
			index++;
		}
	}

	public CustomRegistryMetaInfo<V> writeMeta() {
		var list = new ArrayList<CustomRegistryMetaInfo.TypeInfo<V>>();

		for (var entry : txTypeMap.reference2IntEntrySet()) {
			list.add(new CustomRegistryMetaInfo.TypeInfo<>(entry.getIntValue(), entry.getKey().key(), entry.getKey().version()));
		}

		return new CustomRegistryMetaInfo<>(registryKeys, List.copyOf(list));
	}

	public void readMeta(@Nullable CustomRegistryMetaInfo<V> info) {
		rxTypeMap.clear();
		txTypeMap.clear();

		if (info != null) {
			for (var entry : info.typeInfos()) {
				var type = getType(entry.key());

				if (type != null) {
					rxTypeMap.put(entry.index(), type);
					txTypeMap.put(type, entry.index());
				} else {
					throw new NullPointerException("Missing type " + entry.key().identifier() + " on client side");
				}
			}
		}
	}

	public void updateValues(Map<Identifier, V> map) {
		valueMap.clear();
		rxValueMap.clear();
		txValueMap.clear();

		valueList = new ArrayList<>(typeList.size() + map.size());

		for (var type : typeList) {
			var unit = type.unit();

			if (unit != null) {
				valueList.add(unit);
			}
		}

		for (var entry : map.entrySet()) {
			var key = registryKeys.create(entry.getKey());
			var value = entry.getValue();
			valueList.add(createRef(key, value));
		}

		for (var ref : valueList) {
			valueMap.put(ref.key(), ref);
		}

		valueList = new ArrayList<>(valueMap.values());
		valueList.sort(null);
		valueList = List.copyOf(valueList);

		updateRefs();

		if (syncValues) {
			int index = 2;

			for (var ref : valueList) {
				rxValueMap.put(index, ref);
				txValueMap.put(ref.key(), index);
				index++;
			}
		}
	}

	public CustomRegistryValueInfo<V> writeValues(RegistryAccess registryAccess, PlatformType platformType) {
		var list = new ArrayList<CustomRegistryValueInfo.ValueInfo<V>>();

		for (var ref : valueList) {
			var key = ref.key();
			var value = ref.value();
			var index = getValueIndex(key);

			try {
				var type = getType(ref);

				if (type.unit() == value) {
					list.add(new CustomRegistryValueInfo.ValueInfo<>(index, key, IOUtils.EMPTY_BYTE_ARRAY));
				} else {
					var buf = PlatformHelper.CURRENT.createBuffer(Unpooled.buffer(), registryAccess, platformType);
					type.streamCodec.encode(Cast.to(buf), value);
					var bytes = IOUtils.toByteArray(buf, true);
					list.add(new CustomRegistryValueInfo.ValueInfo<>(index, key, bytes));
				}
			} catch (Exception ex) {
				KLib.LOGGER.error("Failed to write custom registry " + registryKeys.root().identifier() + " value " + value);
			}
		}

		return new CustomRegistryValueInfo<>(registryKeys, List.copyOf(list));
	}

	private void updateRefs() {
		for (var ref : refMap.values()) {
			ref.value = null;
		}

		for (var ref : valueList) {
			var ref0 = refMap.get(ref.key());

			if (ref0 != null && ref0 != ref) {
				ref0.value = ref.value();
			}
		}
	}

	public void readValues(CustomRegistryValueInfo<V> info, RegistryAccess registryAccess, PlatformType platformType) {
		valueMap.clear();
		rxValueMap.clear();
		txValueMap.clear();
		valueList = new ArrayList<>(info.valueInfos().size());

		for (var entry : info.valueInfos()) {
			int index = entry.index();
			var key = entry.key();
			var type = getType(key);

			if (type != null) {
				var unit = type.unit();

				if (unit != null) {
					valueMap.put(key, unit);
					rxValueMap.put(index, unit);
					txValueMap.put(unit.key, index);
				} else {
					var buf = PlatformHelper.CURRENT.createBuffer(Unpooled.wrappedBuffer(entry.value()), registryAccess, platformType);

					try {
						var value = type.streamCodec.decode(Cast.to(buf));
						var ref = createRef(key, value);
						valueMap.put(key, ref);
						rxValueMap.put(index, ref);
						txValueMap.put(ref.key(), index);
					} catch (Exception ex) {
						KLib.LOGGER.error("Failed to decode custom registry entry " + registryKeys.root().identifier() + "/" + key.identifier(), ex);
					}

					buf.release();
				}
			} else {
				KLib.LOGGER.error("Missing custom registry type " + registryKeys.root().identifier() + "/" + key.identifier());
			}
		}

		for (var ref : valueList) {
			valueMap.put(ref.key(), ref);
		}

		valueList = new ArrayList<>(valueMap.values());
		valueList.sort(null);
		valueList = List.copyOf(valueList);
		updateRefs();
	}

	public boolean syncValues() {
		return syncValues;
	}

	public RegistryKeys<V> registryKeys() {
		return registryKeys;
	}

	@Nullable
	public CustomRegistryType<B, V> getOptionalType(Ref<V> ref) {
		if (ref instanceof CustomRegistryType.Unit<?, ?> unit) {
			//noinspection unchecked
			return (CustomRegistryType.Unit<B, V>) unit;
		}

		var value = ref.value();
		return typeProvider == null ? null : typeProvider.apply(value);
	}

	@Nullable
	public CustomRegistryType<B, V> getOptionalType(V value) {
		var type = typeProvider == null ? null : typeProvider.apply(value);

		if (type != null) {
			return type;
		}

		for (var ref : valueList) {
			if (ref instanceof CustomRegistryType.Unit<?, V> unit && ref.optionalValue() == value) {
				//noinspection unchecked
				return (CustomRegistryType.Unit<B, V>) unit;
			}
		}

		return null;
	}

	@Nullable
	public CustomRegistryType<B, V> getType(ResourceKey<V> key) {
		return typeMap.get(key);
	}

	public CustomRegistryType<B, V> getType(Ref<V> value) {
		var type = getOptionalType(value);

		if (type != null) {
			return type;
		}

		throw new NullPointerException("Value " + value.optionalValue() + " does not have a type");
	}

	public CustomRegistryType<B, V> getType(V value) {
		var type = getOptionalType(value);

		if (type != null) {
			return type;
		}

		throw new NullPointerException("Value " + value + " does not have a type");
	}

	@Nullable
	public ResourceKey<V> getKey(V value) {
		var type = getOptionalType(value);
		return type == null ? null : type.key();
	}

	public Codec<V> directCodec() {
		return directCodec;
	}

	public Codec<Ref<V>> codec() {
		return codec;
	}

	public StreamCodec<B, Ref<V>> streamCodec() {
		return streamCodec;
	}

	public DataType<Ref<V>> dataType() {
		return dataType;
	}

	public List<CustomRegistryType<B, V>> typeList() {
		return typeList;
	}

	@Nullable
	public Ref<V> get(ResourceKey<V> key) {
		return valueMap.get(key);
	}

	@Nullable
	public Ref<V> get(Identifier id) {
		return get(registryKeys.create(id));
	}

	public List<Ref<V>> values() {
		return valueList;
	}

	@Override
	public @NonNull Iterator<Ref<V>> iterator() {
		return valueList.iterator();
	}

	@Nullable
	public Ref<V> decodeValue(B buf) {
		int index = VarInt.read(buf);

		if (index == 0) {
			return null;
		} else if (index == 1) {
			var type = decodeType(buf);

			if (type != null) {
				return ref(type.streamCodec().decode(buf));
			}

			return null;
		} else {
			var ref = rxValueMap.get(index);

			if (ref == null) {
				throw new NullPointerException("Value " + registryKeys.root().identifier() + "/" + index + " not found");
			}

			return ref;
		}
	}

	public int getValueIndex(ResourceKey<V> key) {
		return syncValues ? txValueMap.getInt(key) : 0;
	}

	public void encodeValue(B buf, @Nullable Ref<V> ref) {
		if (ref == null) {
			VarInt.write(buf, 0);
			return;
		}

		int index = getValueIndex(ref.key());

		if (index != 0) {
			VarInt.write(buf, index);
		} else {
			VarInt.write(buf, 1);
			var type = getType(ref);
			encodeType(buf, type);

			if (type != null) {
				type.streamCodec().encode(buf, ref.value());
			}
		}
	}

	@Nullable
	public CustomRegistryType<B, V> decodeType(B buf) {
		if (syncValues) {
			int index = VarInt.read(buf);

			if (index == 0) {
				return null;
			}

			var type = rxTypeMap.get(index);

			if (type == null) {
				throw new NullPointerException("Type " + registryKeys.root().identifier() + "/" + index + " not found");
			}

			return type;
		} else {
			var key = registryKeys.streamCodec().decode(buf);
			var type = getType(key);

			if (type == null) {
				throw new NullPointerException("Type " + registryKeys.root().identifier() + "/" + key.identifier() + " not found");
			}

			return type;
		}
	}

	public void encodeType(ByteBuf buf, @Nullable CustomRegistryType<B, V> value) {
		if (syncValues) {
			if (value == null) {
				VarInt.write(buf, 0);
			} else {
				int index = txTypeMap.getInt(value);

				if (index == 0) {
					throw new NullPointerException("Index of " + registryKeys.root().identifier() + "/" + value.key().identifier() + " not found");
				}

				VarInt.write(buf, index);
			}
		} else {
			registryKeys.streamCodec().encode(buf, value == null ? null : value.key());
		}
	}

	@ApiStatus.Internal
	public ArgumentType<Ref<V>> createArgument(CommandBuildContext ctx) {
		if (dataType == null) {
			throw new NullPointerException("Registry " + registryKeys.root().identifier() + " doesn't have a registered DataType");
		}

		var ops = ctx.createSerializationContext(NbtOps.INSTANCE);
		return new CustomRegistryArgument<>(ops, TagParser.create(ops), this);
	}
}
