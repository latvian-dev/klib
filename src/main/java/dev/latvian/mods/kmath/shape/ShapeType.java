package dev.latvian.mods.kmath.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ShapeType(String name, MapCodec<? extends Shape> codec, StreamCodec<ByteBuf, ? extends Shape> streamCodec) implements StringRepresentable {
	public static final Map<String, ShapeType> MAP = Map.copyOf(Stream.of(EmptyShape.TYPE, CubeShape.TYPE, CuboidShape.TYPE, SphereShape.TYPE, CylinderShape.TYPE).collect(Collectors.toMap(ShapeType::name, Function.identity())));

	public static final Codec<ShapeType> CODEC = Codec.STRING.flatXmap(s -> {
		var shape = MAP.get(s);
		return shape == null ? DataResult.error(() -> "Shape not found") : DataResult.success(shape);
	}, s -> DataResult.success(s.name()));

	public static final StreamCodec<ByteBuf, ShapeType> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(MAP::get, ShapeType::name);

	@Override
	public String getSerializedName() {
		return name;
	}
}
