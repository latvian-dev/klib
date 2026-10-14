package dev.latvian.mods.klib.registry;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public abstract sealed class CustomRegistryType<B extends ByteBuf, V> implements WithKey, CustomRegistryOwnTypeProvider<B, V> permits UnitType, DynamicType {
	protected final String key;
	protected MapCodec<V> codec;
	protected StreamCodec<? super B, V> streamCodec;
	protected int version;

	CustomRegistryType(String key) {
		this.key = key;
		this.version = 1;
	}

	@Override
	public CustomRegistryType<B, V> type() {
		return this;
	}

	@Override
	public String key() {
		return key;
	}

	@Override
	public String optionalKey() {
		return key;
	}

	public MapCodec<V> codec() {
		return codec;
	}

	public StreamCodec<? super B, V> streamCodec() {
		return streamCodec;
	}

	@Nullable
	public UnitType<B, V> unit() {
		return null;
	}

	public boolean isUnit() {
		return false;
	}

	public int version() {
		return version;
	}

	@Override
	public String toString() {
		return key;
	}
}
