package dev.latvian.mods.klib.texture;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;

public record OverlayUV(int packed, int u, int v) implements PackedUV {
	public static final OverlayUV NORMAL = new OverlayUV(0xA0000);
	public static final OverlayUV HURT = new OverlayUV(0x30000);
	public static final OverlayUV WHITE = new OverlayUV(0xA00F0);
	public static final OverlayUV WHITE_HURT = new OverlayUV(0x300F0);

	public static final StreamCodec<ByteBuf, OverlayUV> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public OverlayUV decode(ByteBuf buf) {
			int type = buf.readByte() & 0xFF;

			return switch (type) {
				case 1 -> NORMAL;
				case 2 -> HURT;
				case 0 -> new OverlayUV(VarInt.read(buf));
				default -> throw new IllegalStateException("Unexpected value: " + type);
			};
		}

		@Override
		public void encode(ByteBuf buf, OverlayUV value) {
			if (value.equals(NORMAL)) {
				buf.writeByte(1);
			} else if (value.equals(HURT)) {
				buf.writeByte(2);
			} else {
				buf.writeByte(0);
				VarInt.write(buf, value.packed);
			}
		}
	};

	public OverlayUV(int packed) {
		this(packed, packed & 0xFFFF, packed >> 16 & 0xFFFF);
	}

	public OverlayUV(int u, int v) {
		this((u & 0xFF) | ((v & 0xFF) << 16), u, v);
	}

	@Override
	public int hashCode() {
		return packed;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof OverlayUV && packed == ((OverlayUV) o).packed;
	}
}
