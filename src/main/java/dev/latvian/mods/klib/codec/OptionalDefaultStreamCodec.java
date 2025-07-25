package dev.latvian.mods.klib.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record OptionalDefaultStreamCodec<B, V>(StreamCodec<B, V> parent, @Nullable V defaultValue) implements StreamCodec<B, V> {
	public OptionalDefaultStreamCodec(StreamCodec<B, V> parent) {
		this(parent, null);
	}

	@Override
	public V decode(B buf) {
		return ((ByteBuf) buf).readBoolean() ? parent.decode(buf) : defaultValue;
	}

	@Override
	public void encode(B buf, V value) {
		if (!Objects.equals(value, defaultValue)) {
			((ByteBuf) buf).writeBoolean(true);
			parent.encode(buf, value);
		} else {
			((ByteBuf) buf).writeBoolean(false);
		}
	}
}
