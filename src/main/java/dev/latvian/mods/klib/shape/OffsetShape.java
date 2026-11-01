package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.JOMLCodecs;
import dev.latvian.mods.klib.codec.JOMLStreamCodecs;
import dev.latvian.mods.klib.registry.Ref;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3fc;

public record OffsetShape(Vector3fc offset, Ref<Shape> shape) {
	public static final Codec<OffsetShape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		JOMLCodecs.VEC3C.fieldOf("offset").forGetter(OffsetShape::offset),
		Shape.CODEC.fieldOf("shape").forGetter(OffsetShape::shape)
	).apply(instance, OffsetShape::new));

	public static final StreamCodec<ByteBuf, OffsetShape> STREAM_CODEC = CompositeStreamCodec.of(
		JOMLStreamCodecs.VEC3C, OffsetShape::offset,
		Shape.STREAM_CODEC, OffsetShape::shape,
		OffsetShape::new
	);
}
