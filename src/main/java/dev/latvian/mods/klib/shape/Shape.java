package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.color.Gradient;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.util.ID;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

public interface Shape {
	CustomRegistry<ByteBuf, Shape> REGISTRY = CustomRegistry.<ByteBuf, Shape>builder()
		.keys(ID.klib("shape"), KLib.ID)
		.type(Shape::type)
		.server()
		.build();

	Codec<Shape> CODEC = REGISTRY.codec();
	StreamCodec<ByteBuf, Shape> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Shape> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC);

	static void builtInTypes(CustomRegistryTypeCollector<ByteBuf, Shape> registry) {
		registry.register(EmptyShape.TYPE);
		registry.register(CubeShape.TYPE);
		registry.register(CuboidShape.TYPE);
		registry.register(CircleShape.TYPE);
		registry.register(SphereShape.TYPE);
		registry.register(CylinderShape.TYPE);
		registry.register(LineShape.TYPE);
		registry.register(QuadrilaterallyFacedConvexHexahedra.TYPE);
		registry.register(VoxelShapeBox.TYPE);

		registry.register(ID.klib("unit_cube"), CubeShape.UNIT_CUBE);
		registry.register(ID.klib("unit_square"), CuboidShape.UNIT_SQUARE);
		registry.register(ID.klib("unit_sphere"), SphereShape.UNIT_SPHERE);
		registry.register(ID.klib("unit_cylinder"), CylinderShape.UNIT_CYLINDER);
		registry.register(ID.klib("unit_circle"), CircleShape.UNIT_CIRCLE);
		registry.register(ID.klib("line_down"), LineShape.DOWN_UNIT);
		registry.register(ID.klib("line_up"), LineShape.UP_UNIT);
		registry.register(ID.klib("line_north"), LineShape.NORTH_UNIT);
		registry.register(ID.klib("line_south"), LineShape.SOUTH_UNIT);
		registry.register(ID.klib("line_west"), LineShape.WEST_UNIT);
		registry.register(ID.klib("line_east"), LineShape.EAST_UNIT);
	}

	@Nullable
	default CustomRegistryType<ByteBuf, Shape> type() {
		return null;
	}

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

	default ColoredShape colored(Gradient quads, Gradient lines) {
		return ColoredShape.of(this, quads, lines);
	}

	default ColoredShape quads(Gradient color) {
		return ColoredShape.quads(this, color);
	}

	default ColoredShape lines(Gradient color) {
		return ColoredShape.lines(this, color);
	}
}
