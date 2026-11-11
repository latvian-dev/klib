package dev.latvian.mods.klib.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.codec.KLibCodecErrors;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.command.CustomRegistryArgument;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.io.CompressionMethod;
import dev.latvian.mods.klib.io.IOUtils;
import dev.latvian.mods.klib.net.KLibByteBuf;
import dev.latvian.mods.klib.net.S2CPacketBundleBuilder;
import dev.latvian.mods.klib.net.S2CPacketConsumer;
import dev.latvian.mods.klib.net.SyncCustomRegistryMetaPayload;
import dev.latvian.mods.klib.net.SyncCustomRegistryValuesPayload;
import dev.latvian.mods.klib.platform.PlatformHelper;
import dev.latvian.mods.klib.platform.PlatformType;
import dev.latvian.mods.klib.util.Cast;
import dev.latvian.mods.klib.util.MiscUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class CustomRegistry<B extends ByteBuf, V> implements Iterable<Ref<V>> {
	private static final DataResult<Ref<?>> EMPTY_REF_ERROR = KLibCodecErrors.error("Empty ref");
	private static final DataResult<Ref<?>> UNBOUND_VALUE_ERROR = KLibCodecErrors.error("Unbound value");
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
			registry.buildMeta();
		}
	}

	public static void syncAll(Player player, S2CPacketConsumer callback) {
		var registryAccess = player.registryAccess();
		var platformType = PlatformHelper.INSTANCE.getPlatformOf(player);
		var metaList = new ArrayList<CustomRegistryMetaInfo>();

		for (var registry : CustomRegistry.ALL.values()) {
			metaList.add(registry.writeMeta());
		}

		callback.s2c(new SyncCustomRegistryMetaPayload(metaList));

		for (var registry : CustomRegistry.ALL.values()) {
			callback.s2c(new SyncCustomRegistryValuesPayload(registry.writeValues(registryAccess, platformType)));
		}
	}

	public static void syncAll(ServerPlayer player) {
		var packets = new S2CPacketBundleBuilder();
		syncAll(player, packets);
		packets.send(player.connection::send);
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
		private CustomRegistryTypeProvider<B, T> typeProvider = null;
		private UnaryOperator<Codec<T>> directCodecFactory = UnaryOperator.identity();
		private CustomRegistryType<B, T> defaultType = null;

		private Builder(String registryId) {
			this.registryId = registryId;
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

		public CustomRegistry<B, T> build() {
			return new CustomRegistry<>(
				registryId,
				typeProvider,
				directCodecFactory,
				defaultType
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

	private boolean isBound;
	private final String registryId;
	private final CustomRegistryTypeProvider<B, V> typeProvider;
	private final CustomRegistryType<B, V> defaultType;

	private final SequencedMap<String, RefOfKey<V>> keyRefMap;
	private final SequencedMap<String, CustomRegistryType<B, V>> typeMap;
	private List<CustomRegistryType<B, V>> typeList;
	private final Int2ObjectMap<CustomRegistryType<B, V>> rxTypeMap;
	private final Reference2IntMap<CustomRegistryType<B, V>> txTypeMap;

	public final Codec<CustomRegistryType<B, V>> typeCodec;
	private final Codec<V> directCodec;
	private final Codec<Ref<V>> codec;
	private final StreamCodec<B, Ref<V>> streamCodec;
	private final DataType<Ref<V>> dataType;

	private final Map<String, Ref<V>> serverValueMap;
	private final Map<String, Ref<V>> clientValueMap;
	private List<String> keyList;
	private List<Ref<V>> valueList;
	private final Int2ObjectMap<Ref<V>> rxValueMap;
	private final Reference2IntMap<String> txValueMap;

	private CustomRegistry(
		String registryId,
		@Nullable CustomRegistryTypeProvider<B, V> typeProvider,
		UnaryOperator<Codec<V>> directCodecFactory,
		@Nullable CustomRegistryType<B, V> defaultType
	) {
		this.isBound = false;
		this.registryId = registryId;
		this.typeProvider = typeProvider;
		this.defaultType = defaultType;

		this.keyRefMap = new Reference2ObjectLinkedOpenHashMap<>();
		this.typeMap = new Reference2ObjectLinkedOpenHashMap<>();
		this.typeList = List.of();
		this.rxTypeMap = new Int2ObjectOpenHashMap<>();
		this.txTypeMap = new Reference2IntOpenHashMap<>();

		this.typeCodec = KLibCodecs.map(typeMap, KLibCodecs.INTERN_PATH, CustomRegistryType::key);
		var unitCodec = KLibCodecs.INTERN_PATH.flatXmap(this::resolveUnitRef, Ref::unitKeyResult);
		var typeField = this.defaultType instanceof DynamicType<?, ?> ? typeCodec.optionalFieldOf("type", this.defaultType) : typeCodec.fieldOf("type");
		this.directCodec = directCodecFactory.apply(typeField.dispatch(this::getType, CustomRegistryType::mapCodec));

		var optionalDirectCodec = directCodec.flatComapMap(this::resolveValueRef, ref -> {
			var value = ref.optionalValue();

			if (value != null) {
				return DataResult.success(value);
			} else {
				return Cast.to(UNBOUND_VALUE_ERROR);
			}
		});

		var unresolvedValueCodec = KLibCodecs.INTERN_PATH.flatXmap(string -> {
			if (string.isEmpty()) {
				return Cast.to(EMPTY_REF_ERROR);
			} else {
				return DataResult.success(ref(string));
			}
		}, ref -> {
			var key = ref.optionalKey();

			if (key.isEmpty()) {
				return KLibCodecErrors.EMPTY_STRING;
			} else {
				return DataResult.success(key);
			}
		});

		this.codec = KLibCodecs.or(List.of(unitCodec, optionalDirectCodec, unresolvedValueCodec));
		this.streamCodec = new ValueStreamCodec<>(this);
		this.dataType = DataType.of(codec, Cast.to(streamCodec));

		this.serverValueMap = new Reference2ObjectLinkedOpenHashMap<>(0);
		this.clientValueMap = new Reference2ObjectLinkedOpenHashMap<>(0);
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
		return ref(value).optimizedRef();
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

		synchronized (this) {
			return keyRefMap.computeIfAbsent(ikey, this::createRefOfKey);
		}
	}

	@SuppressWarnings("StringEquality")
	private RefOfKey<V> createRefOfKey(String key) {
		var ref = new RefOfKey<V>(key);

		for (var value : valueList) {
			if (value.key() == key) {
				ref.value = value.optionalValue();
				break;
			}
		}

		return ref;
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
		updateValues(List.of(), RefType.SERVER);
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

	public synchronized void updateValues(Collection<? extends Map.Entry<String, ? extends V>> entries, RefType refType) {
		Map<String, Ref<V>> valueMap;

		if (refType == RefType.SERVER) {
			valueMap = serverValueMap;
		} else if (refType == RefType.CLIENT) {
			valueMap = clientValueMap;
		} else {
			throw new IllegalArgumentException("Invalid ref type " + refType);
		}

		valueMap.clear();

		for (var entry : entries) {
			if (!(ref(entry.getKey()) instanceof RefOfKey<V> ref)) {
				throw new IllegalStateException("Tried to update values containing unit type " + entry.getKey());
			}

			ref.value = entry.getValue();
			valueMap.put(ref.key(), ref);
		}

		updateValuesAndRefs();

		if (refType == RefType.SERVER) {
			rxValueMap.clear();
			txValueMap.clear();

			var netList = new ArrayList<>(valueMap.values());
			netList.sort(WithKey.COMPARATOR);

			int networkId = 1;

			for (var ref : netList) {
				rxValueMap.put(networkId, ref);
				txValueMap.put(ref.key(), networkId);
				networkId++;
			}
		}
	}

	public synchronized CustomRegistryValueInfo writeValues(RegistryAccess registryAccess, PlatformType platformType) {
		var list = new ArrayList<CustomRegistryValueInfo.ValueInfo>();

		for (var ref : serverValueMap.values()) {
			var key = ref.key();
			var value = ref.value();
			var index = getValueIndex(key);

			try {
				var type = getType(ref);
				var typeId = txTypeMap.getInt(type);
				var buf = PlatformHelper.INSTANCE.createBuffer(Unpooled.buffer(), registryAccess, platformType);
				type.streamCodec.encode(Cast.to(buf), value);
				var bytes = IOUtils.toByteArray(buf, true);
				list.add(new CustomRegistryValueInfo.ValueInfo(index, typeId, key, bytes));
			} catch (Exception ex) {
				KLib.LOGGER.error("Failed to write custom registry " + registryId + " value " + value);
			}
		}

		return new CustomRegistryValueInfo(registryId, list);
	}

	private void updateValuesAndRefs() {
		var newValueList = new ArrayList<Ref<V>>(serverValueMap.size() + clientValueMap.size() + typeList.size() + 1);
		newValueList.addAll(serverValueMap.values());
		newValueList.addAll(clientValueMap.values());

		for (var type : typeList) {
			if (type != defaultType) {
				var unit = type.unit();

				if (unit != null) {
					newValueList.add(unit);
				}
			}
		}

		newValueList.sort(WithKey.COMPARATOR);

		if (defaultType instanceof UnitType<B, V> unit) {
			newValueList.addFirst(unit);
		}

		var newKeyList = new ArrayList<String>(newValueList.size());

		for (var ref : newValueList) {
			newKeyList.add(ref.key());
		}

		synchronized (this) {
			keyList = List.copyOf(newKeyList);
			valueList = List.copyOf(newValueList);

			for (var ref : keyRefMap.values()) {
				ref.value = null;
			}

			for (var ref : newValueList) {
				var ref0 = keyRefMap.get(ref.key());

				if (ref0 != null && ref0 != ref) {
					ref0.value = ref.value();
				}
			}
		}
	}

	public synchronized void readValues(CustomRegistryValueInfo info, RegistryAccess registryAccess, PlatformType platformType) {
		var entries = new ArrayList<Map.Entry<String, V>>();

		rxValueMap.clear();
		txValueMap.clear();

		for (var entry : info.valueInfos()) {
			int networkId = entry.networkId();
			var key = entry.key().intern();
			var type = rxTypeMap.get(entry.typeId());

			if (type != null) {
				ByteBuf buf = null;

				try {
					if (!(ref(key) instanceof RefOfKey<V> ref)) {
						throw new IllegalStateException("Invalid ref, tried to decode a unit value");
					}

					buf = KLibByteBuf.with(Unpooled.wrappedBuffer(entry.value()), registryAccess, platformType, ref);
					ref.value = type.streamCodec.decode(Cast.to(buf));
					entries.add(Map.entry(key, ref.value));
					rxValueMap.put(networkId, ref);
					txValueMap.put(ref.key(), networkId);
				} catch (Exception ex) {
					KLib.LOGGER.error("Failed to decode custom registry entry " + registryId + ":" + key, ex);
				} finally {
					if (buf != null) {
						buf.release();
					}
				}
			} else {
				KLib.LOGGER.error("Missing custom registry type " + registryId + ":" + key);
			}
		}

		updateValues(entries, RefType.SERVER);
	}

	@Nullable
	public CustomRegistryType<B, V> getOptionalType(Ref<V> ref) {
		if (ref instanceof CustomRegistryOwnTypeProvider<?, ?> provider) {
			//noinspection unchecked
			return (CustomRegistryType<B, V>) provider.type();
		}

		return getOptionalType(ref.optionalValue());
	}

	@Nullable
	public CustomRegistryType<B, V> getOptionalType(@Nullable V value) {
		if (value == null) {
			return null;
		} else if (value instanceof CustomRegistryOwnTypeProvider<?, ?> provider) {
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

		for (var ref : values()) {
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

	public synchronized List<Ref<V>> values() {
		return valueList;
	}

	public synchronized List<String> keys() {
		return keyList;
	}

	@Override
	public @NotNull Iterator<Ref<V>> iterator() {
		return values().iterator();
	}

	public void encodeValue(B buf, @Nullable Ref<V> ref) {
		if (ref == null) {
			VarInt.write(buf, 0);
		} else if (ref == defaultType) {
			VarInt.write(buf, 1);
		} else if (ref instanceof UnitType<?, ?> unit) {
			VarInt.write(buf, 2);
			encodeType(buf, (UnitType) unit);
		} else if (ref instanceof RefOfKey<V> v && v.value == null) {
			VarInt.write(buf, 6);
			KLibStreamCodecs.INTERN_STRING.encode(buf, v.key());
		} else {
			int index = getValueIndex(ref.optionalKey());

			if (index != 0) {
				VarInt.write(buf, 3);
				VarInt.write(buf, index);
			} else {
				var type = getType(ref);

				if (type == null) {
					throw new IllegalStateException("Can't encode null type of " + ref);
				}

				try {
					var vbuf = PlatformHelper.INSTANCE.createBuffer(Unpooled.buffer(), buf);
					type.streamCodec.encode(Cast.to(vbuf), ref.value());
					var bytes = IOUtils.toByteArray(vbuf, true);
					var compressed = CompressionMethod.ZSTD.compress(bytes);

					VarInt.write(buf, 4);
					encodeType(buf, type);

					if (compressed.length < bytes.length) {
						VarInt.write(buf, CompressionMethod.ZSTD.id);
						FriendlyByteBuf.writeByteArray(buf, compressed);
					} else {
						VarInt.write(buf, CompressionMethod.NONE.id);
						FriendlyByteBuf.writeByteArray(buf, bytes);
					}
				} catch (Exception ex) {
					throw new IllegalStateException("Failed to encode " + ref, ex);
				}
			}
		}
	}

	@Nullable
	public Ref<V> decodeValue(B buf) {
		int index = VarInt.read(buf);

		if (index == 0) {
			return null;
		} else if (index == 1) {
			if (defaultType == null || defaultType.unit() == null) {
				throw new IllegalStateException("Value " + registryId + ": No default unit type");
			} else {
				return defaultType.unit();
			}
		} else if (index == 2) {
			var type = decodeType(buf);

			if (type == null) {
				throw new NullPointerException("Value " + registryId + ": Unknown type");
			} else if (type.unit() == null) {
				throw new NullPointerException("Value " + registryId + ": Type '" + type + "' is not a unit type");
			} else {
				return type.unit();
			}
		} else if (index == 3) {
			var networkId = VarInt.read(buf);
			var ref = rxValueMap.get(networkId);

			if (ref == null) {
				throw new NullPointerException("Value " + registryId + ":" + networkId + " not found");
			} else {
				return ref;
			}
		} else if (index == 4) {
			var type = decodeType(buf);

			if (type == null) {
				throw new NullPointerException("Value " + registryId + ": Unknown type");
			} else {
				var compression = CompressionMethod.of(VarInt.read(buf));
				var bytes = FriendlyByteBuf.readByteArray(buf);

				if (compression != CompressionMethod.NONE) {
					try {
						bytes = compression.decompress(bytes);
					} catch (IOException ex) {
						throw new IllegalArgumentException("Value " + registryId + ": Failed to decompress bytes", ex);
					}
				}

				var bbuf = PlatformHelper.INSTANCE.createBuffer(Unpooled.wrappedBuffer(bytes), buf);
				var value = type.streamCodec.decode(Cast.to(bbuf));
				return ref(value);
			}
		} else if (index == 5) {
			var tag = ByteBufCodecs.TAG.decode(buf);
			var registryAccess = buf instanceof RegistryFriendlyByteBuf reg ? reg.registryAccess() : MiscUtils.STATIC_REGISTRY_ACCESS;
			var ops = registryAccess.createSerializationContext(NbtOps.INSTANCE);

			try {
				return codec.parse(ops, tag).getOrThrow();
			} catch (Exception ex) {
				throw new IllegalArgumentException("Value " + registryId + ": Failed to decode from " + tag, ex);
			}
		} else if (index == 6) {
			return ref(KLibStreamCodecs.INTERN_STRING.decode(buf));
		} else {
			throw new IllegalArgumentException("Value " + registryId + ": Invalid index " + index);
		}
	}

	public int getTypeIndex(@Nullable CustomRegistryType<B, V> value) {
		return value != null ? txTypeMap.getInt(value) : 0;
	}

	public int getValueIndex(String key) {
		return txValueMap.getInt(key.intern());
	}

	public void encodeType(ByteBuf buf, @Nullable CustomRegistryType<B, V> value) {
		if (value == null) {
			VarInt.write(buf, 0);
		} else {
			int index = txTypeMap.getInt(value);

			if (index == 0) {
				throw new NullPointerException("Index of " + registryId + ":" + value.key() + " not found");
			}

			VarInt.write(buf, index);
		}
	}

	@Nullable
	public CustomRegistryType<B, V> decodeType(B buf) {
		int index = VarInt.read(buf);

		if (index == 0) {
			return null;
		}

		var type = rxTypeMap.get(index);

		if (type == null) {
			throw new NullPointerException("Type " + registryId + ":" + index + " not found");
		}

		return type;
	}

	@ApiStatus.Internal
	public ArgumentType<Ref<V>> createArgument(CommandBuildContext ctx) {
		if (dataType == null) {
			throw new NullPointerException("Registry " + registryId + " doesn't have a registered DataType");
		}

		var ops = ctx.createSerializationContext(NbtOps.INSTANCE);
		return new CustomRegistryArgument<>(ops, TagParser.create(ops), this);
	}

	public void syncValues(ServerPlayer player, S2CPacketConsumer callback) {
		var registryAccess = player.registryAccess();
		var platformType = PlatformHelper.INSTANCE.getPlatformOf(player);
		callback.s2c(new SyncCustomRegistryValuesPayload(writeValues(registryAccess, platformType)));
	}

	@Override
	public String toString() {
		return "CustomRegistry[" + registryId + "]";
	}

	public void setAllProperties(int key, @Nullable Object value) {
		for (var type : typeList) {
			if (type instanceof DynamicType<?, ?> t) {
				t.setProperty(key, value);
			}
		}
	}
}
