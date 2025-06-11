package dev.latvian.mods.klib.shape;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3fc;

import java.util.function.Function;

public interface Shape {
	Codec<Shape> DIRECT_CODEC = ShapeType.CODEC.dispatch("type", Shape::type, ShapeType::codec);
	Codec<Shape> UNIT_OR_DIRECT_CODEC = Codec.either(UnitShape.CODEC, DIRECT_CODEC).xmap(e -> e.map(Function.identity(), Function.identity()), s -> {
		if (s instanceof UnitShape us) {
			return Either.left(us);
		} else if (UnitShape.REF_MAP.get(s) instanceof UnitShape us) {
			return Either.left(us);
		} else {
			return Either.right(s);
		}
	});
	Codec<Shape> CODEC = UNIT_OR_DIRECT_CODEC.xmap(Shape::optimize, Function.identity());
	StreamCodec<ByteBuf, Shape> DIRECT_STREAM_CODEC = ShapeType.STREAM_CODEC.dispatch(Shape::type, ShapeType::streamCodec);
	StreamCodec<ByteBuf, Shape> STREAM_CODEC = ByteBufCodecs.either(UnitShape.STREAM_CODEC, DIRECT_STREAM_CODEC).map(e -> e.map(Function.identity(), Function.identity()), s -> s instanceof UnitShape u ? Either.left(u) : Either.right(s));
	DataType<Shape> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, Shape.class);

	ShapeType type();

	default Shape optimize() {
		return this;
	}

	void buildLines(float x, float y, float z, VertexCallback callback);

	void buildQuads(float x, float y, float z, VertexCallback callback);

	default boolean contains(Vector3fc p) {
		return false;
	}

	default boolean isVisible(double x, double y, double z, FrustumCheck frustum) {
		return true;
	}
}
