package dev.latvian.mods.klib.registry;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class CustomRegistryType<B extends ByteBuf, V> implements WithKey<V>, CustomRegistryOwnTypeProvider<B, V> {
	public static final class Unit<B extends ByteBuf, V> extends CustomRegistryType<B, V> implements Ref<V>, CustomRegistryOwnTypeProvider<B, V> {
		private final V instance;

		Unit(ResourceKey<V> key, Function<CustomRegistryType<B, V>, V> factory) {
			super(key);
			this.instance = factory.apply(this);
			this.codec = MapCodec.unit(instance);
			this.streamCodec = StreamCodec.unit(instance);
		}

		@Override
		public Unit<B, V> unit() {
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
			return String.valueOf(instance);
		}
	}

	public static class Dynamic<B extends ByteBuf, V> extends CustomRegistryType<B, V> {
		Dynamic(ResourceKey<V> key, MapCodec<V> codec, StreamCodec<? super B, V> streamCodec) {
			super(key);
			this.codec = codec;
			this.streamCodec = streamCodec;
		}

		public Dynamic<B, V> version(int version) {
			this.version = version;
			return this;
		}

		public Dynamic<B, V> backwardsCompatibility(int version, StreamCodec<? super B, V> streamCodec) {
			throw new IllegalStateException("Not yet implemented");
		}
	}

	protected final ResourceKey<V> key;
	protected MapCodec<V> codec;
	protected StreamCodec<? super B, V> streamCodec;
	protected int version;

	private CustomRegistryType(ResourceKey<V> key) {
		this.key = key;
		this.version = 1;
	}

	@Override
	public CustomRegistryType<B, V> type() {
		return this;
	}

	@Override
	public ResourceKey<V> key() {
		return key;
	}

	@Override
	public ResourceKey<V> optionalKey() {
		return key;
	}

	public MapCodec<V> codec() {
		return codec;
	}

	public StreamCodec<? super B, V> streamCodec() {
		return streamCodec;
	}

	@Nullable
	public Unit<B, V> unit() {
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
		return id().toString();
	}
}
