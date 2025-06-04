package dev.latvian.mods.kmath.codec;

import com.google.common.base.Suppliers;
import dev.latvian.mods.kmath.KMath;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface KMathStreamCodecs {
	static <B, V> StreamCodec<B, V> optional(StreamCodec<B, V> self, @Nullable V defaultValue) {
		return new StreamCodec<>() {
			@Override
			public V decode(B buf) {
				return ((ByteBuf) buf).readBoolean() ? self.decode(buf) : defaultValue;
			}

			@Override
			public void encode(B buf, V value) {
				if (!Objects.equals(value, defaultValue)) {
					((ByteBuf) buf).writeBoolean(true);
					self.encode(buf, value);
				} else {
					((ByteBuf) buf).writeBoolean(false);
				}
			}
		};
	}

	static <B, V> StreamCodec<B, V> lazy(com.google.common.base.Supplier<StreamCodec<B, V>> supplier) {
		return new StreamCodec<>() {
			private final com.google.common.base.Supplier<StreamCodec<B, V>> cached = Suppliers.memoize(supplier);

			@Override
			public V decode(B buf) {
				return cached.get().decode(buf);
			}

			@Override
			public void encode(B buf, V value) {
				cached.get().encode(buf, value);
			}
		};
	}

	StreamCodec<ByteBuf, Float> FLOAT_OR_ZERO = optional(ByteBufCodecs.FLOAT, 0F);
	StreamCodec<ByteBuf, Float> FLOAT_OR_ONE = optional(ByteBufCodecs.FLOAT, 1F);

	StreamCodec<ByteBuf, Double> DOUBLE_OR_ZERO = optional(ByteBufCodecs.DOUBLE, 0D);
	StreamCodec<ByteBuf, Double> DOUBLE_OR_ONE = optional(ByteBufCodecs.DOUBLE, 1D);

	StreamCodec<ByteBuf, Vec3> VEC3 = new StreamCodec<>() {
		@Override
		public Vec3 decode(ByteBuf buf) {
			return KMath.vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
		}

		@Override
		public void encode(ByteBuf buf, Vec3 value) {
			buf.writeDouble(value.x);
			buf.writeDouble(value.y);
			buf.writeDouble(value.z);
		}
	};
}
