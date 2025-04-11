package dev.latvian.mods.kmath.texture;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;

public record LightUV(int light, int overlay, int lightU, int lightV, int overlayU, int overlayV) {
	public static final LightUV NORMAL = new LightUV(0xF00000, 0xA0000);
	public static final LightUV FULLBRIGHT = new LightUV(0xF000F0, 0xA0000);
	public static final LightUV NORMAL_HURT = new LightUV(0xF00000, 0x30000);
	public static final LightUV FULLBRIGHT_HURT = new LightUV(0xF000F0, 0x30000);

	public static LightUV get(boolean fullbright, boolean hurt) {
		return fullbright ? (hurt ? FULLBRIGHT_HURT : FULLBRIGHT) : (hurt ? NORMAL_HURT : NORMAL);
	}

	public static final StreamCodec<ByteBuf, LightUV> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public LightUV decode(ByteBuf buf) {
			int type = buf.readByte() & 0xFF;

			return switch (type) {
				case 0 -> NORMAL;
				case 1 -> FULLBRIGHT;
				case 2 -> NORMAL_HURT;
				case 3 -> FULLBRIGHT_HURT;
				case 4 -> new LightUV(VarInt.read(buf), VarInt.read(buf));
				default -> throw new IllegalStateException("Unexpected value: " + type);
			};
		}

		@Override
		public void encode(ByteBuf buf, LightUV value) {
			if (value.equals(NORMAL)) {
				buf.writeByte(0);
			} else if (value.equals(FULLBRIGHT)) {
				buf.writeByte(1);
			} else if (value.equals(NORMAL_HURT)) {
				buf.writeByte(2);
			} else if (value.equals(FULLBRIGHT_HURT)) {
				buf.writeByte(3);
			} else {
				buf.writeByte(4);
				VarInt.write(buf, value.light);
				VarInt.write(buf, value.overlay);
			}
		}
	};

	public LightUV(int light, int overlay) {
		this(light, overlay, light & '\uffff', light >> 16 & '\uffff', overlay & '\uffff', overlay >> 16 & '\uffff');
	}
}
