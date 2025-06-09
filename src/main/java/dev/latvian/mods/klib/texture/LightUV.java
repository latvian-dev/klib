package dev.latvian.mods.klib.texture;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;

public record LightUV(int packed, int u, int v) implements PackedUV {
	public static final LightUV NONE = new LightUV(0xF00000);
	public static final LightUV BRIGHT = new LightUV(0xF000F0);
	public static final LightUV FULL_SKY = new LightUV(15728640);
	public static final LightUV FULL_BLOCK = new LightUV(240);

	public static final StreamCodec<ByteBuf, LightUV> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public LightUV decode(ByteBuf buf) {
			int type = buf.readByte() & 0xFF;

			return switch (type) {
				case 1 -> NONE;
				case 2 -> BRIGHT;
				case 3 -> FULL_SKY;
				case 4 -> FULL_BLOCK;
				case 0 -> new LightUV(VarInt.read(buf));
				default -> throw new IllegalStateException("Unexpected value: " + type);
			};
		}

		@Override
		public void encode(ByteBuf buf, LightUV value) {
			if (value.equals(NONE)) {
				buf.writeByte(1);
			} else if (value.equals(BRIGHT)) {
				buf.writeByte(2);
			} else if (value.equals(FULL_SKY)) {
				buf.writeByte(3);
			} else if (value.equals(FULL_BLOCK)) {
				buf.writeByte(4);
			} else {
				buf.writeByte(0);
				VarInt.write(buf, value.packed);
			}
		}
	};

	public LightUV(int packed) {
		this(packed, packed & 0xFFFF, packed >> 16 & 0xFFFF);
	}

	public LightUV(int u, int v) {
		this((u & 0xFF) | ((v & 0xFF) << 16), u, v);
	}

	@Override
	public int hashCode() {
		return packed;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof LightUV && packed == ((LightUV) o).packed;
	}
}
