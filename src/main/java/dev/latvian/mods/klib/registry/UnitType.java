package dev.latvian.mods.klib.registry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public final class UnitType<B extends ByteBuf, V> extends CustomRegistryType<B, V> implements Ref<V>, CustomRegistryOwnTypeProvider<B, V> {
	public static <B extends ByteBuf, V> UnitType<B, V> create(String id, Function<UnitType<B, V>, V> instance) {
		return new UnitType<>(id.intern(), instance);
	}

	public static <B extends ByteBuf, V> UnitType<B, V> create(String id, V instance) {
		return new UnitType<>(id.intern(), _ -> instance);
	}

	private final V instance;
	private final DataResult<String> unitKeyResult;

	UnitType(String key, Function<UnitType<B, V>, V> factory) {
		super(key);
		this.instance = factory.apply(this);
		this.codec = MapCodec.unit(instance);
		this.streamCodec = StreamCodec.unit(instance);
		this.unitKeyResult = DataResult.success(key);
	}

	@Override
	public UnitType<B, V> type() {
		return this;
	}

	@Override
	public UnitType<B, V> unit() {
		return this;
	}

	@Override
	public boolean isUnit() {
		return true;
	}

	@Override
	public V optionalValue() {
		return instance;
	}

	@Override
	public V value() {
		return instance;
	}

	@Override
	public String toString() {
		return key;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	@SuppressWarnings("StringEquality")
	public boolean equals(Object obj) {
		if (obj instanceof CustomRegistryType<?, ?> ut) {
			return key == ut.key;
		} else if (obj instanceof Ref<?> ref) {
			return key == ref.optionalKey();
		} else {
			return false;
		}
	}

	@Override
	public DataResult<String> unitKeyResult() {
		return unitKeyResult;
	}
}