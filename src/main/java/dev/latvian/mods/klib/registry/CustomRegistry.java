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
import dev.latvian.mods.klib.util.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomRegistry<B extends ByteBuf, V> {
	private static final Map<ResourceKey<? extends Registry<?>>, CustomRegistry<?, ?>> ALL0 = new Reference2ObjectLinkedOpenHashMap<>();
	public static final Map<ResourceKey<? extends Registry<?>>, CustomRegistry<?, ?>> ALL = Collections.unmodifiableMap(ALL0);
	private static final Map<DataType<?>, CustomRegistry<?, ?>> DATA_TYPE_TO_REGISTRY0 = new Reference2ObjectLinkedOpenHashMap<>();
	public static final Map<DataType<?>, CustomRegistry<?, ?>> DATA_TYPE_TO_REGISTRY = Collections.unmodifiableMap(DATA_TYPE_TO_REGISTRY0);

	public static void registerAll(Consumer<CustomRegistryCollector> callback) {
		ALL0.clear();
		DATA_TYPE_TO_REGISTRY0.clear();

		callback.accept(new CustomRegistryCollector() {
			@Override
			public <T> void register(DataType<T> dataType, CustomRegistry<?, T> registry) {
				ALL0.put(registry.registryKeys().root(), registry);
				DATA_TYPE_TO_REGISTRY0.put(dataType, registry);
				registry.dataType = dataType;
			}
		});
	}

	public static void buildAllMeta() {
		for (var registry : ALL.values()) {
			if (registry.side().isServer()) {
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
			if (registry.side().isServer()) {
				metaList.add(registry.writeMeta());
			}
		}

		packets.add(new ClientboundCustomPayloadPacket(new SyncCustomRegistryMetaPayload(metaList)));

		for (var registry : CustomRegistry.ALL.values()) {
			if (registry.side().isServer()) {
				packets.add(new ClientboundCustomPayloadPacket(new SyncCustomRegistryValuesPayload(registry.writeValues(registryAccess, platformType))));
			}
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

	public static class Builder<B extends ByteBuf, T> {
		private RegistryKeys<T> registryKeys;
		private Side side = null;
		private CustomRegistryTypeProvider<B, T> typeProvider;

		public Builder<B, T> keys(RegistryKeys<T> registryKeys) {
			this.registryKeys = registryKeys;
			return this;
		}

		public Builder<B, T> keys(Identifier id, String commonNamespace) {
			return keys(RegistryKeys.createKeys(id, commonNamespace));
		}

		public Builder<B, T> client() {
			this.side = Side.CLIENT;
			return this;
		}

		public Builder<B, T> server() {
			this.side = Side.SERVER;
			return this;
		}

		public Builder<B, T> type(CustomRegistryTypeProvider<B, T> typeProvider) {
			this.typeProvider = typeProvider;
			return this;
		}

		public CustomRegistry<B, T> build() {
			return new CustomRegistry<>(Objects.requireNonNull(side, "You must specify .client() or .server()"), registryKeys, typeProvider);
		}
	}

	public static <B extends ByteBuf, T> Builder<B, T> builder() {
		return new Builder<>();
	}

	private final Side side;
	private final RegistryKeys<V> registryKeys;
	private final CustomRegistryTypeProvider<B, V> typeProvider;
	DataType<V> dataType;

	private final Map<ResourceKey<V>, CustomRegistryType<B, V>> typeMap;
	private final Map<V, CustomRegistryType<B, V>> unitTypeMap;
	private final Int2ObjectMap<CustomRegistryType<B, V>> rxTypeMap;
	private final Reference2IntMap<CustomRegistryType<B, V>> txTypeMap;

	public final Codec<CustomRegistryType<B, V>> typeCodec;
	private final Codec<V> codec;
	private final StreamCodec<B, V> streamCodec;

	private final Map<ResourceKey<V>, V> valueMap;
	private final Int2ObjectMap<V> rxValueMap;
	private final Reference2IntMap<V> txValueMap;
	private List<ResourceKey<V>> sortedKeys;

	private CustomRegistry(Side side, RegistryKeys<V> registryKeys, @Nullable CustomRegistryTypeProvider<B, V> typeProvider) {
		this.side = side;
		this.registryKeys = registryKeys;
		this.typeProvider = typeProvider;

		this.typeMap = new Reference2ObjectLinkedOpenHashMap<>();
		this.unitTypeMap = new Reference2ObjectLinkedOpenHashMap<>();
		this.rxTypeMap = new Int2ObjectOpenHashMap<>();
		this.txTypeMap = new Reference2IntOpenHashMap<>();

		this.typeCodec = KLibCodecs.map(typeMap, registryKeys.codec(), CustomRegistryType::key);

		Codec<V> unitCodec = registryKeys.codec().flatXmap(key -> {
			var type = typeMap.get(key);

			if (type == null) {
				return DataResult.error(() -> "Value " + key + " not found");
			}

			var unit = type.instance();

			if (unit != null) {
				return DataResult.success(unit);
			} else {
				return DataResult.error(() -> "Type " + key + " is not a unit type");
			}
		}, value -> {
			var type = unitTypeMap.get(value);

			if (type != null) {
				return DataResult.success(type.key());
			} else {
				return DataResult.error(() -> "Type " + (typeProvider == null ? null : typeProvider.apply(value)) + " of " + value + " is not a unit type");
			}
		});

		Codec<V> directCodec = typeCodec.dispatch("type", this::getType, CustomRegistryType::codec);

		this.codec = KLibCodecs.or(unitCodec, directCodec);

		this.streamCodec = new StreamCodec<>() {
			@Override
			public void encode(B buf, V value) {
				encodeValue(buf, value);
			}

			@Override
			public V decode(B buf) {
				return decodeValue(buf);
			}
		};

		this.valueMap = new Reference2ObjectLinkedOpenHashMap<>();
		this.rxValueMap = new Int2ObjectOpenHashMap<>();
		this.txValueMap = new Reference2IntOpenHashMap<>();
		this.sortedKeys = List.of();
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

	public void registerTypes(Consumer<CustomRegistryTypeCollector<B, V>> callback) {
		typeMap.clear();
		unitTypeMap.clear();
		rxTypeMap.clear();
		txTypeMap.clear();
		var list = new ArrayList<CustomRegistryType<B, V>>();
		callback.accept(new CustomRegistryTypeCollectorImpl<>(this, list));
		list.sort((o1, o2) -> o1.id().compareNamespaced(o2.id()));

		for (var type : list) {
			typeMap.put(type.key(), type);
			V unitValue = type.instance();

			if (unitValue != null) {
				unitTypeMap.put(unitValue, type);
			}
		}

		updateValues(Map.of());
	}

	private void buildMeta() {
		rxTypeMap.clear();
		txTypeMap.clear();

		int index = 2;

		for (var entry : typeMap.entrySet()) {
			var type = entry.getValue();
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
				var type = typeMap.get(entry.key());

				if (type != null) {
					rxTypeMap.put(entry.index(), type);
					txTypeMap.put(type, entry.index());
				}
			}
		}
	}

	public void updateValues(Map<Identifier, V> map) {
		valueMap.clear();
		rxValueMap.clear();
		txValueMap.clear();

		var valueList = new ArrayList<Map.Entry<ResourceKey<V>, V>>(unitTypeMap.size() + map.size());

		for (var unit : unitTypeMap.values()) {
			valueList.add(Map.entry(unit.key(), unit.instance()));
		}

		for (var entry : map.entrySet()) {
			var key = registryKeys.create(entry.getKey());
			var value = entry.getValue();
			valueList.add(Map.entry(key, value));
		}

		valueList.sort((o1, o2) -> o1.getKey().identifier().compareNamespaced(o2.getKey().identifier()));
		int index = 2;

		for (var entry : valueList) {
			var value = entry.getValue();

			if (side.isServer()) {
				rxValueMap.put(index, value);
				txValueMap.put(value, index);
			}

			valueMap.put(entry.getKey(), value);
			index++;
		}

		sortedKeys = List.copyOf(valueMap.keySet());
	}

	public CustomRegistryValueInfo<V> writeValues(RegistryAccess registryAccess, PlatformType platformType) {
		var list = new ArrayList<CustomRegistryValueInfo.ValueInfo<V>>();

		for (var entry : valueMap.entrySet()) {
			var value = entry.getValue();
			var index = getValueIndex(value);

			try {
				var type = getType(value);

				if (type.instance() == value) {
					list.add(new CustomRegistryValueInfo.ValueInfo<>(index, type.key(), IOUtils.EMPTY_BYTE_ARRAY));
				} else {
					var buf = PlatformHelper.CURRENT.createBuffer(Unpooled.buffer(), registryAccess, platformType);
					type.streamCodec.encode(Cast.to(buf), value);
					var bytes = IOUtils.toByteArray(buf, true);
					list.add(new CustomRegistryValueInfo.ValueInfo<>(index, type.key(), bytes));
				}
			} catch (Exception ex) {
				KLib.LOGGER.error("Failed to write custom registry " + registryKeys.root().identifier() + " value " + value);
			}
		}

		return new CustomRegistryValueInfo<>(registryKeys, List.copyOf(list));
	}

	public void readValues(CustomRegistryValueInfo<V> info, RegistryAccess registryAccess, PlatformType platformType) {
		valueMap.clear();
		rxValueMap.clear();
		txValueMap.clear();

		for (var entry : info.valueInfos()) {
			int index = entry.index();
			var key = entry.key();
			var type = typeMap.get(key);

			if (type != null) {
				var unit = type.instance();

				if (unit != null) {
					valueMap.put(key, unit);
					rxValueMap.put(index, unit);
					txValueMap.put(unit, index);
				} else {
					var buf = PlatformHelper.CURRENT.createBuffer(Unpooled.wrappedBuffer(entry.value()), registryAccess, platformType);

					try {
						var value = type.streamCodec.decode(Cast.to(buf));
						valueMap.put(key, value);
						rxValueMap.put(index, value);
						txValueMap.put(value, index);
					} catch (Exception ex) {
						KLib.LOGGER.error("Failed to decode custom registry entry " + registryKeys.root().identifier() + "/" + key.identifier(), ex);
					}

					buf.release();
				}
			} else {
				KLib.LOGGER.error("Missing custom registry type " + registryKeys.root().identifier() + "/" + key.identifier());
			}
		}

		sortedKeys = new ArrayList<>(valueMap.keySet());
		sortedKeys.sort((o1, o2) -> o1.identifier().compareNamespaced(o2.identifier()));
		sortedKeys = List.copyOf(sortedKeys);
	}

	public Side side() {
		return side;
	}

	public RegistryKeys<V> registryKeys() {
		return registryKeys;
	}

	@Nullable
	public CustomRegistryType<B, V> getOptionalType(V value) {
		var type = typeProvider == null ? null : typeProvider.apply(value);

		if (type != null && type.instance() == value) {
			return type;
		}

		var unitType = unitTypeMap.get(value);

		if (unitType != null) {
			return unitType;
		}

		return type;
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

	public Codec<V> codec() {
		return codec;
	}

	public StreamCodec<B, V> streamCodec() {
		return streamCodec;
	}

	public DataType<V> dataType() {
		return dataType;
	}

	public Map<ResourceKey<V>, CustomRegistryType<B, V>> typeMap() {
		return typeMap;
	}

	public Map<ResourceKey<V>, V> valueMap() {
		return valueMap;
	}

	public List<ResourceKey<V>> sortedKeys() {
		return sortedKeys;
	}

	@Nullable
	public V decodeValue(B buf) {
		int index = VarInt.read(buf);

		if (index == 0) {
			return null;
		} else if (index == 1) {
			var type = decodeType(buf);

			if (type != null) {
				return type.streamCodec().decode(buf);
			}

			return null;
		} else {
			var value = rxValueMap.get(index);

			if (value == null) {
				throw new NullPointerException("Value " + registryKeys.root().identifier() + "/" + index + " not found");
			}

			return value;
		}
	}

	public int getValueIndex(V value) {
		return side.isClient() ? 0 : txValueMap.getInt(value);
	}

	public void encodeValue(B buf, @Nullable V value) {
		if (value == null) {
			VarInt.write(buf, 0);
		}

		int index = getValueIndex(value);

		if (index != 0) {
			VarInt.write(buf, index);
		} else {
			VarInt.write(buf, 1);
			var type = value == null ? null : getType(value);
			encodeType(buf, type);

			if (type != null) {
				type.streamCodec().encode(buf, value);
			}
		}
	}

	@Nullable
	public CustomRegistryType<B, V> decodeType(B buf) {
		if (side.isServer()) {
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
			var type = typeMap.get(key);

			if (type == null) {
				throw new NullPointerException("Type " + registryKeys.root().identifier() + "/" + key.identifier() + " not found");
			}

			return type;
		}
	}

	public void encodeType(ByteBuf buf, @Nullable CustomRegistryType<B, V> value) {
		if (side.isServer()) {
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
	public ArgumentType<V> createArgument(CommandBuildContext ctx) {
		if (dataType == null) {
			throw new NullPointerException("Registry " + registryKeys.root().identifier() + " doesn't have a registered DataType");
		}

		var ops = ctx.createSerializationContext(NbtOps.INSTANCE);
		return new CustomRegistryArgument<>(ops, TagParser.create(ops), this);
	}
}
