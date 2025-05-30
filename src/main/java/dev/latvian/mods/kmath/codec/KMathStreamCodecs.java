package dev.latvian.mods.kmath.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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

	StreamCodec<ByteBuf, Float> FLOAT_OR_ZERO = optional(ByteBufCodecs.FLOAT, 0F);
	StreamCodec<ByteBuf, Float> FLOAT_OR_ONE = optional(ByteBufCodecs.FLOAT, 1F);

	StreamCodec<ByteBuf, Double> DOUBLE_OR_ZERO = optional(ByteBufCodecs.DOUBLE, 0D);
	StreamCodec<ByteBuf, Double> DOUBLE_OR_ONE = optional(ByteBufCodecs.DOUBLE, 1D);
}
