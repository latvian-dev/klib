package dev.latvian.mods.klib.registry;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public abstract class CustomRegistryType<B extends ByteBuf, V> {
	public static class Unit<B extends ByteBuf, V> extends CustomRegistryType<B, V> {
		private final V instance;

		Unit(ResourceKey<V> key, Function<CustomRegistryType<B, V>, V> factory) {
			super(key);
			this.instance = factory.apply(this);
			this.codec = MapCodec.unit(instance);
			this.streamCodec = StreamCodec.unit(instance);
		}

		@Override
		public V instance() {
			return instance;
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

	private final ResourceKey<V> key;
	protected MapCodec<V> codec;
	protected StreamCodec<? super B, V> streamCodec;
	protected int version;

	private CustomRegistryType(ResourceKey<V> key) {
		this.key = key;
		this.version = 1;
	}

	public ResourceKey<V> key() {
		return key;
	}

	public MapCodec<V> codec() {
		return codec;
	}

	public StreamCodec<? super B, V> streamCodec() {
		return streamCodec;
	}

	public Identifier id() {
		return key.identifier();
	}

	@Nullable
	public V instance() {
		return null;
	}

	public V instanceOrThrow() {
		return Objects.requireNonNull(instance());
	}

	public int version() {
		return version;
	}

	@Override
	public String toString() {
		return id().toString();
	}
}
