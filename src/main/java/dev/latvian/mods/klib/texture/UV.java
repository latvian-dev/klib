package dev.latvian.mods.klib.texture;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.math.Vec3f;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record UV(float u0, float v0, float u1, float v1) {
	public static final UV FULL = new UV(0F, 0F, 1F, 1F);
	public static final UV ZERO = new UV(0F, 0F, 0F, 0F);
	public static final UV ONE = new UV(1F, 1F, 1F, 1F);

	public static final Codec<UV> CODEC = Codec.FLOAT.listOf(4, 4).xmap(f -> {
		float u0 = f.get(0);
		float v0 = f.get(1);
		float u1 = f.get(2);
		float v1 = f.get(3);

		if (u0 == 0F && v0 == 0F && u1 == 1F && v1 == 1F) {
			return FULL;
		} else if (u0 == 0F && v0 == 0F && u1 == 0F && v1 == 0F) {
			return ZERO;
		} else if (u0 == 1F && v0 == 1F && u1 == 1F && v1 == 1F) {
			return ONE;
		} else {
			return new UV(u0, v0, u1, v1);
		}
	}, uv -> List.of(uv.u0, uv.v0, uv.u1, uv.v1));

	public static final StreamCodec<ByteBuf, UV> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public UV decode(ByteBuf buf) {
			return switch (buf.readByte()) {
				case 1 -> FULL;
				case 2 -> ZERO;
				case 3 -> ONE;
				case 4 -> new UV(buf.readFloat());
				case 5 -> new UV(0F, 0F, buf.readFloat(), buf.readFloat());
				default -> new UV(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
			};
		}

		@Override
		public void encode(ByteBuf buf, UV uv) {
			if (uv.isFull()) {
				buf.writeByte(1);
			} else if (uv.equals(ZERO)) {
				buf.writeByte(2);
			} else if (uv.equals(ONE)) {
				buf.writeByte(3);
			} else if (uv.u1 == uv.u0 && uv.v0 == uv.u0 && uv.v1 == uv.u0) {
				buf.writeByte(4);
				buf.writeFloat(uv.u0);
			} else if (uv.u0 == 0F && uv.v0 == 0F) {
				buf.writeByte(5);
				buf.writeFloat(uv.u1);
				buf.writeFloat(uv.v1);
			} else {
				buf.writeByte(0);
				buf.writeFloat(uv.u0);
				buf.writeFloat(uv.v0);
				buf.writeFloat(uv.u1);
				buf.writeFloat(uv.v1);
			}
		}
	};

	public UV(float all) {
		this(all, all, all, all);
	}

	public UV uvsByFace(Vec3f from, Vec3f to, Direction face) {
		return switch (face) {
			case DOWN -> new UV(from.x(), 1F - to.z(), to.x(), 1F - from.z());
			case UP -> new UV(from.x(), from.z(), to.x(), to.z());
			case NORTH -> new UV(1F - to.x(), 1F - to.y(), 1F - from.x(), 1F - from.y());
			case SOUTH -> new UV(from.x(), 1F - to.y(), to.x(), 1F - from.y());
			case WEST -> new UV(from.z(), 1F - to.y(), to.z(), 1F - from.y());
			case EAST -> new UV(1F - to.z(), 1F - to.y(), 1F - from.z(), 1F - from.y());
		};
	}

	public UV mul(UV uv) {
		return new UV(
			KMath.lerp(uv.u0, u0, u1),
			KMath.lerp(uv.v0, v0, v1),
			KMath.lerp(uv.u1, u0, u1),
			KMath.lerp(uv.v1, v0, v1)
		);
	}

	public float u(float delta) {
		return KMath.lerp(delta, u0, u1);
	}

	public float v(float delta) {
		return KMath.lerp(delta, v0, v1);
	}

	public boolean isFull() {
		return u0 == 0F && v0 == 0F && u1 == 1F && v1 == 1F;
	}

	@Override
	public String toString() {
		return "[" + u0 + "," + v0 + "," + u1 + "," + v1 + "]";
	}
}
