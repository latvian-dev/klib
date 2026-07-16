package dev.latvian.mods.klib.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.command.CustomRegistryArgument;
import dev.latvian.mods.klib.core.KLibFriendlyByteBuf;
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
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class CustomRegistry<B extends ByteBuf, V> implements Iterable<Ref<V>> {
	private static final Map<String, CustomRegistry<?, ?>> ALL0 = new Reference2ObjectLinkedOpenHashMap<>();
	public static final Map<String, CustomRegistry<?, ?>> ALL = Collections.unmodifiableMap(ALL0);
	private static final Map<DataType<?>, CustomRegistry<?, ?>> DATA_TYPE_TO_REGISTRY0 = new Reference2ObjectLinkedOpenHashMap<>();
	public static final Map<DataType<?>, CustomRegistry<?, ?>> DATA_TYPE_TO_REGISTRY = Collections.unmodifiableMap(DATA_TYPE_TO_REGISTRY0);

	public static void registerAll(Consumer<CustomRegistryCollector> callback) {
		ALL0.clear();
		DATA_TYPE_TO_REGISTRY0.clear();
		var list = new ArrayList<CustomRegistryCollector.Entry<?, ?>>();
		callback.accept(list::add);

		for (var entry : list) {
			ALL0.put(entry.registry().registryId, entry.registry());
			DATA_TYPE_TO_REGISTRY0.put(entry.registry().dataType, entry.registry());
		}

		for (var entry : list) {
			entry.registry().registerTypes(Cast.to(entry.callback()));
		}
	}

	public static void buildAllMeta() {
		for (var registry : ALL.values()) {
			if (registry.syncValues()) {
				registry.buildMeta();
			}
		}
	}

	public static void syncAll(ServerPlayer player) {
		var registryAccess = player.registryAccess();
		var platformType = PlatformHelper.CURRENT.getPlatformOf(player);
		var packets = new ArrayList<Packet<? super ClientGamePacketListener>>();
		var metaList = new ArrayList<CustomRegistryMetaInfo>();

		for (var registry : CustomRegistry.ALL.values()) {
			metaList.add(registry.writeMeta());
		}

		packets.add(new ClientboundCustomPayloadPacket(new SyncCustomRegistryMetaPayload(metaList)));

		for (var registry : CustomRegistry.ALL.values()) {
			packets.add(new ClientboundCustomPayloadPacket(new SyncCustomRegistryValuesPayload(registry.writeValues(registryAccess, platformType))));
		}

		player.connection.send(new ClientboundBundlePacket(packets));
	}

	private record CustomRegistryTypeCollectorImpl<B extends ByteBuf, V>(CustomRegistry<B, V> registry, List<CustomRegistryType<B, V>> list) implements CustomRegistryTypeCollector<B, V> {
		@Override
		public void register(CustomRegistryType<B, V> type) {
			list.add(type);
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
		private final String registryId;
		private boolean syncValues = true;
		private CustomRegistryTypeProvider<B, T> typeProvider = null;
		private UnaryOperator<Codec<T>> directCodecFactory = UnaryOperator.identity();
		private CustomRegistryType<B, T> defaultType = null;
		private final List<Runnable> updateCallbacks = new ArrayList<>(0);

		private Builder(String registryId) {
			this.registryId = registryId;
		}

		public Builder<B, T> noValueSync() {
			this.syncValues = false;
			return this;
		}

		public Builder<B, T> type(CustomRegistryTypeProvider<B, T> typeProvider) {
			this.typeProvider = typeProvider;
			return this;
		}

		public Builder<B, T> customCodec(UnaryOperator<Codec<T>> directCodecFactory) {
			this.directCodecFactory = directCodecFactory;
			return this;
		}

		public Builder<B, T> customCodec(Codec<T> customCodec) {
			return customCodec(directCodec -> KLibCodecs.or(customCodec, directCodec));
		}

		public Builder<B, T> defaultType(CustomRegistryType<B, T> defaultType) {
			this.defaultType = defaultType;
			return this;
		}

		public Builder<B, T> updateCallback(Runnable callback) {
			this.updateCallbacks.add(callback);
			return this;
		}

		public CustomRegistry<B, T> build() {
			return new CustomRegistry<>(
				registryId,
				typeProvider,
				syncValues,
				directCodecFactory,
				defaultType,
				List.copyOf(updateCallbacks)
			);
		}
	}

	public static <B extends ByteBuf, T> Builder<B, T> builder(String registryId) {
		if (registryId == null || registryId.isEmpty()) {
			throw new NullPointerException("registryId cannot be null or empty");
		}

		return new Builder<>(registryId.intern());
	}

	public static <B extends ByteBuf, T> CustomRegistry<B, T> create(String registryId) {
		return CustomRegistry.<B, T>builder(registryId).build();
	}

	public static <B extends ByteBuf, T> CustomRegistry<B, T> create(String registryId, CustomRegistryType<B, T> defaultType) {
		return CustomRegistry.<B, T>builder(registryId).defaultType(defaultType).build();
	}

	public static <B extends ByteBuf, T> CustomRegistry<B, T> create(String registryId, UnaryOperator<Codec<T>> directCodecFactory) {
		return CustomRegistry.<B, T>builder(registryId).customCodec(directCodecFactory).build();
	}

	public static <B extends ByteBuf, T> CustomRegistry<B, T> createNoValueSync(String registryId, CustomRegistryType<B, T> defaultType) {
		return CustomRegistry.<B, T>builder(registryId).defaultType(defaultType).noValueSync().build();
	}

	private boolean isBound;
	private final String registryId;
	private final CustomRegistryTypeProvider<B, V> typeProvider;
	private final boolean syncValues;
	private final CustomRegistryType<B, V> defaultType;
	private final List<Runnable> updateCallbacks;

	private final SequencedMap<String, RefOfKey<V>> refMap;
	private final SequencedMap<String, CustomRegistryType<B, V>> typeMap;
	private List<CustomRegistryType<B, V>> typeList;
	private final Int2ObjectMap<CustomRegistryType<B, V>> rxTypeMap;
	private final Reference2IntMap<CustomRegistryType<B, V>> txTypeMap;

	public final Codec<CustomRegistryType<B, V>> typeCodec;
	private final Codec<V> directCodec;
	private final Codec<Ref<V>> codec;
	private final StreamCodec<B, Ref<V>> streamCodec;
	private final DataType<Ref<V>> dataType;

	private final Map<String, Ref<V>> valueMap;
	private List<String> keyList;
	private List<Ref<V>> valueList;
	private final Int2ObjectMap<Ref<V>> rxValueMap;
	private final Reference2IntMap<String> txValueMap;

	private CustomRegistry(
		String registryId,
		@Nullable CustomRegistryTypeProvider<B, V> typeProvider,
		boolean syncValues,
		UnaryOperator<Codec<V>> directCodecFactory,
		@Nullable CustomRegistryType<B, V> defaultType,
		List<Runnable> updateCallbacks
	) {
		this.isBound = false;
		this.registryId = registryId;
		this.typeProvider = typeProvider;
		this.syncValues = syncValues;
		this.defaultType = defaultType;
		this.updateCallbacks = updateCallbacks;

		this.refMap = new Reference2ObjectLinkedOpenHashMap<>();
		this.typeMap = new Reference2ObjectLinkedOpenHashMap<>();
		this.typeList = List.of();
		this.rxTypeMap = new Int2ObjectOpenHashMap<>();
		this.txTypeMap = new Reference2IntOpenHashMap<>();

		this.typeCodec = KLibCodecs.map(typeMap, KLibCodecs.INTERN_PATH, CustomRegistryType::key);
		var unitCodec = KLibCodecs.INTERN_PATH.flatXmap(this::resolveUnitRef, Ref::unitKeyResult);
		var typeField = this.defaultType instanceof DynamicType<?, ?> ? typeCodec.optionalFieldOf("type", this.defaultType) : typeCodec.fieldOf("type");
		this.directCodec = directCodecFactory.apply(typeField.dispatch(this::getType, CustomRegistryType::codec));
		this.codec = KLibCodecs.or(unitCodec, this.directCodec.xmap(this::resolveValueRef, Ref::value));
		this.streamCodec = new ValueStreamCodec<>(this);
		this.dataType = DataType.of(codec, Cast.to(streamCodec));

		this.valueMap = new Reference2ObjectOpenHashMap<>();
		this.keyList = List.of();
		this.valueList = List.of();
		this.rxValueMap = new Int2ObjectOpenHashMap<>();
		this.txValueMap = new Reference2IntOpenHashMap<>();
	}

	public String registryId() {
		return registryId;
	}

	private DataResult<Ref<V>> resolveUnitRef(String key) {
		if (this.defaultType instanceof UnitType<B, V> unit && key.isEmpty()) {
			return DataResult.success(unit);
		}

		var type = typeMap.get(key);

		if (type == null) {
			return DataResult.error(() -> "Value " + key + " not found");
		}

		var unit = type.unit();

		if (unit != null) {
			return DataResult.success(unit);
		} else {
			return DataResult.error(() -> "Type " + key + " is not a unit type");
		}
	}

	private Ref<V> resolveValueRef(V value) {
		if (value instanceof RefOptimizer<?> o) {
			value = (V) o.optimize();
		}

		return ref(value);
	}

	public Ref<V> ref(String key) {
		if (!isBound) {
			throw new IllegalStateException("Called .ref() before .registerTypes() of registry " + registryId + " was called");
		}

		var ikey = key.intern();
		var type = typeMap.get(ikey);

		if (type instanceof WithRef<?> withRef) {
			//noinspection unchecked
			return (Ref<V>) withRef.ref();
		}

		return refMap.computeIfAbsent(ikey, RefOfKey::new);
	}

	public Ref<V> ref(V value) {
		if (value instanceof WithRef<?> withRef) {
			//noinspection unchecked
			return (Ref<V>) withRef.ref();
		}

		return valueRef(value);
	}

	public Ref<V> valueRef(V value) {
		for (var ref : valueList) {
			if (ref.optionalValue() == value) {
				return ref;
			}
		}

		return new RefOfValue<>(value);
	}

	public void registerTypes(@Nullable Consumer<CustomRegistryTypeCollector<B, V>> callback) {
		if (isBound) {
			throw new IllegalStateException("Can't register types twice of registry " + registryId);
		}

		if (callback == null && defaultType == null) {
			throw new NullPointerException("Callback can't be null without a default type");
		}

		typeMap.clear();
		rxTypeMap.clear();
		txTypeMap.clear();
		typeList = new ArrayList<>();

		if (defaultType != null) {
			typeList.add(defaultType);
		}

		if (callback != null) {
			callback.accept(new CustomRegistryTypeCollectorImpl<>(this, typeList));
		}

		typeList.sort(WithKey.COMPARATOR);

		for (var type : typeList) {
			if (typeMap.put(type.key(), type) != null) {
				throw new IllegalStateException("Duplicate type key: " + registryId + ":" + type.key());
			}
		}

		typeList = List.copyOf(typeMap.values());
		updateValues(Map.of());
		isBound = true;
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

	public CustomRegistryMetaInfo writeMeta() {
		var list = new ArrayList<CustomRegistryMetaInfo.TypeInfo>();

		for (var entry : txTypeMap.reference2IntEntrySet()) {
			list.add(new CustomRegistryMetaInfo.TypeInfo(entry.getIntValue(), entry.getKey().key(), entry.getKey().version()));
		}

		return new CustomRegistryMetaInfo(registryId, List.copyOf(list));
	}

	public void readMeta(@Nullable CustomRegistryMetaInfo info) {
		rxTypeMap.clear();
		txTypeMap.clear();

		if (info != null) {
			for (var entry : info.typeInfos()) {
				var type = getType(entry.key());

				if (type != null) {
					rxTypeMap.put(entry.index(), type);
					txTypeMap.put(type, entry.index());
				} else {
					throw new NullPointerException("Missing type " + entry.key() + " on client side");
				}
			}
		}
	}

	public void updateValues(Map<Identifier, V> map) {
		valueMap.clear();
		rxValueMap.clear();
		txValueMap.clear();

		keyList = List.of();
		valueList = new ArrayList<>(typeList.size() + map.size());

		for (var type : typeList) {
			var unit = type.unit();

			if (unit != null) {
				valueList.add(unit);
			}
		}

		for (var entry : map.entrySet()) {
			if (!(ref(entry.getKey().getPath()) instanceof RefOfKey<V> ref)) {
				throw new IllegalStateException("Tried to update values containing unit type " + entry.getKey().getPath());
			}

			ref.value = entry.getValue();
			valueList.add(ref);
		}

		for (var ref : valueList) {
			valueMap.put(ref.key(), ref);
		}

		updateValuesAndRefs();

		if (syncValues) {
			int index = 2;

			for (var ref : valueList) {
				rxValueMap.put(index, ref);
				txValueMap.put(ref.key(), index);
				index++;
			}
		}

		for (var callback : updateCallbacks) {
			callback.run();
		}
	}

	public CustomRegistryValueInfo writeValues(RegistryAccess registryAccess, PlatformType platformType) {
		var list = new ArrayList<CustomRegistryValueInfo.ValueInfo>();

		for (var ref : valueList) {
			var key = ref.key();
			var value = ref.value();
			var index = getValueIndex(key);

			try {
				var type = getType(ref);

				if (type.unit() == value) {
					list.add(new CustomRegistryValueInfo.ValueInfo(index, key, IOUtils.EMPTY_BYTE_ARRAY));
				} else {
					var buf = PlatformHelper.CURRENT.createBuffer(Unpooled.buffer(), registryAccess, platformType);
					type.streamCodec.encode(Cast.to(buf), value);
					var bytes = IOUtils.toByteArray(buf, true);
					list.add(new CustomRegistryValueInfo.ValueInfo(index, key, bytes));
				}
			} catch (Exception ex) {
				KLib.LOGGER.error("Failed to write custom registry " + registryId + " value " + value);
			}
		}

		return new CustomRegistryValueInfo(registryId, List.copyOf(list));
	}

	private void updateValuesAndRefs() {
		keyList = new ArrayList<>(valueMap.size());
		valueList = new ArrayList<>(valueMap.values());

		if (defaultType instanceof UnitType<B, V> unit) {
			valueList.remove(unit);
		}

		valueList.sort(WithKey.COMPARATOR);

		if (defaultType instanceof UnitType<B, V> unit) {
			valueList.addFirst(unit);
		}

		for (var ref : valueList) {
			keyList.add(ref.key());
		}

		keyList = List.copyOf(keyList);
		valueList = List.copyOf(valueList);

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

	public void readValues(CustomRegistryValueInfo info, RegistryAccess registryAccess, PlatformType platformType) {
		valueMap.clear();
		rxValueMap.clear();
		txValueMap.clear();
		keyList = List.of();
		valueList = new ArrayList<>(info.valueInfos().size());

		for (var entry : info.valueInfos()) {
			int index = entry.index();
			var key = entry.key().intern();
			var type = typeMap.get(key);

			if (type != null) {
				var unit = type.unit();

				if (unit != null) {
					valueMap.put(key, unit);
					rxValueMap.put(index, unit);
					txValueMap.put(unit.key, index);
				} else {
					var buf = PlatformHelper.CURRENT.createBuffer(Unpooled.wrappedBuffer(entry.value()), registryAccess, platformType);

					try {
						if (!(ref(key) instanceof RefOfKey<V> ref)) {
							throw new IllegalStateException("Invalid ref, tried to decode a unit value");
						}

						KLibFriendlyByteBuf.set(buf, ref);
						ref.value = type.streamCodec.decode(Cast.to(buf));
						valueMap.put(key, ref);
						rxValueMap.put(index, ref);
						txValueMap.put(ref.key(), index);
					} catch (Exception ex) {
						KLib.LOGGER.error("Failed to decode custom registry entry " + registryId + ":" + key, ex);
					}

					buf.release();
				}
			} else {
				KLib.LOGGER.error("Missing custom registry type " + registryId + ":" + key);
			}
		}

		for (var ref : valueList) {
			valueMap.put(ref.key(), ref);
		}

		updateValuesAndRefs();

		for (var callback : updateCallbacks) {
			callback.run();
		}
	}

	public boolean syncValues() {
		return syncValues;
	}

	@Nullable
	public CustomRegistryType<B, V> getOptionalType(Ref<V> ref) {
		if (ref instanceof CustomRegistryOwnTypeProvider<?, ?> provider) {
			//noinspection unchecked
			return (CustomRegistryType<B, V>) provider.type();
		}

		return getOptionalType(ref.value());
	}

	@Nullable
	public CustomRegistryType<B, V> getOptionalType(V value) {
		if (value instanceof CustomRegistryOwnTypeProvider<?, ?> provider) {
			var type = provider.type();

			if (type != null) {
				//noinspection unchecked
				return (CustomRegistryType<B, V>) type;
			}
		}

		var type = typeProvider == null ? null : typeProvider.apply(value);

		if (type != null) {
			return type;
		}

		for (var ref : valueList) {
			if (ref instanceof UnitType<?, V> unit && ref.optionalValue() == value) {
				//noinspection unchecked
				return (UnitType<B, V>) unit;
			}
		}

		return null;
	}

	@Nullable
	public CustomRegistryType<B, V> getType(String key) {
		return typeMap.get(key.intern());
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

	public String getKey(V value) {
		var type = getOptionalType(value);
		return type == null ? "" : type.key();
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
	public Ref<V> get(String key) {
		return valueMap.get(key.intern());
	}

	public List<Ref<V>> values() {
		return valueList;
	}

	public List<String> keys() {
		return keyList;
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
				KLibFriendlyByteBuf.set((FriendlyByteBuf) buf, this);

				try {
					var value = type.streamCodec.decode(buf);
					return ref(value);
				} finally {
					KLibFriendlyByteBuf.set((FriendlyByteBuf) buf, null);
				}
			}

			return null;
		} else {
			var ref = rxValueMap.get(index);

			if (ref == null) {
				throw new NullPointerException("Value " + registryId + ":" + index + " not found");
			}

			return ref;
		}
	}

	public int getValueIndex(String key) {
		return syncValues ? txValueMap.getInt(key.intern()) : 0;
	}

	public void encodeValue(B buf, @Nullable Ref<V> ref) {
		if (ref == null) {
			VarInt.write(buf, 0);
			return;
		}

		int index = getValueIndex(ref.optionalKey());

		if (index != 0) {
			VarInt.write(buf, index);
		} else {
			VarInt.write(buf, 1);
			var type = getType(ref);
			encodeType(buf, type);

			if (type != null) {
				type.streamCodec.encode(buf, ref.value());
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
				throw new NullPointerException("Type " + registryId + ":" + index + " not found");
			}

			return type;
		} else {
			var key = KLibStreamCodecs.INTERN_STRING.decode(buf);
			var type = typeMap.get(key);

			if (type == null) {
				throw new NullPointerException("Type " + registryId + ":" + key + " not found");
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
					throw new NullPointerException("Index of " + registryId + ":" + value.key() + " not found");
				}

				VarInt.write(buf, index);
			}
		} else {
			KLibStreamCodecs.INTERN_STRING.encode(buf, value == null ? "" : value.key());
		}
	}

	@ApiStatus.Internal
	public ArgumentType<Ref<V>> createArgument(CommandBuildContext ctx) {
		if (dataType == null) {
			throw new NullPointerException("Registry " + registryId + " doesn't have a registered DataType");
		}

		var ops = ctx.createSerializationContext(NbtOps.INSTANCE);
		return new CustomRegistryArgument<>(ops, TagParser.create(ops), this);
	}

	public void syncValues(ServerPlayer player) {
		var registryAccess = player.registryAccess();
		var platformType = PlatformHelper.CURRENT.getPlatformOf(player);
		player.connection.send(new ClientboundCustomPayloadPacket(new SyncCustomRegistryValuesPayload(writeValues(registryAccess, platformType))));
	}

	@Override
	public String toString() {
		return "CustomRegistry[" + registryId + "]";
	}
}
