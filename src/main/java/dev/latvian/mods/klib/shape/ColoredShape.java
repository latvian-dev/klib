package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.color.Gradient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;

public record ColoredShape(Shape shape, Gradient quads, Gradient lines) {
	public static final Codec<ColoredShape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Shape.DIRECT_CODEC.fieldOf("shape").forGetter(ColoredShape::shape),
		Gradient.CODEC.optionalFieldOf("quads", Color.TRANSPARENT).forGetter(ColoredShape::quads),
		Gradient.CODEC.optionalFieldOf("lines", Color.TRANSPARENT).forGetter(ColoredShape::lines)
	).apply(instance, ColoredShape::new));

	public static final StreamCodec<ByteBuf, ColoredShape> STREAM_CODEC = StreamCodec.composite(
		Shape.STREAM_CODEC, ColoredShape::shape,
		Gradient.STREAM_CODEC, ColoredShape::quads,
		Gradient.STREAM_CODEC, ColoredShape::lines,
		ColoredShape::new
	);

	public static ColoredShape of(Shape shape, Gradient quads, Gradient lines) {
		return new ColoredShape(shape, quads, lines);
	}

	public static ColoredShape quads(Shape shape, Gradient color) {
		return new ColoredShape(shape, color, Color.TRANSPARENT);
	}

	public static ColoredShape lines(Shape shape, Gradient color) {
		return new ColoredShape(shape, Color.TRANSPARENT, color);
	}

	public PositionedColoredShape at(Vec3 pos) {
		return new PositionedColoredShape(pos, this);
	}

	public PositionedColoredShape at(Vector3dc pos) {
		return new PositionedColoredShape(new Vec3(pos.x(), pos.y(), pos.z()), this);
	}
}
