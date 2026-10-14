package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.gradient.Gradient;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.registry.Ref;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;

public record ColoredShape(Ref<Shape> shape, Ref<Gradient> quads, Ref<Gradient> lines) {
	public static final Codec<ColoredShape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Shape.CODEC.fieldOf("shape").forGetter(ColoredShape::shape),
		Gradient.CODEC.optionalFieldOf("quads", Gradient.EMPTY).forGetter(ColoredShape::quads),
		Gradient.CODEC.optionalFieldOf("lines", Gradient.EMPTY).forGetter(ColoredShape::lines)
	).apply(instance, ColoredShape::new));

	public static final StreamCodec<ByteBuf, ColoredShape> STREAM_CODEC = CompositeStreamCodec.of(
		Shape.STREAM_CODEC, ColoredShape::shape,
		Gradient.STREAM_CODEC, ColoredShape::quads,
		Gradient.STREAM_CODEC, ColoredShape::lines,
		ColoredShape::new
	);

	public static ColoredShape of(Ref<Shape> shape, Ref<Gradient> quads, Ref<Gradient> lines) {
		return new ColoredShape(shape, quads, lines);
	}

	public static ColoredShape quads(Ref<Shape> shape, Ref<Gradient> color) {
		return new ColoredShape(shape, color, Gradient.EMPTY);
	}

	public static ColoredShape lines(Ref<Shape> shape, Ref<Gradient> color) {
		return new ColoredShape(shape, Gradient.EMPTY, color);
	}

	public PositionedColoredShape at(Vec3 pos) {
		return new PositionedColoredShape(pos, this);
	}

	public PositionedColoredShape at(Vector3dc pos) {
		return new PositionedColoredShape(KMath.vec3(pos.x(), pos.y(), pos.z()), this);
	}

	public PositionedColoredShape at(double x, double y, double z) {
		return new PositionedColoredShape(KMath.vec3(x, y, z), this);
	}
}
