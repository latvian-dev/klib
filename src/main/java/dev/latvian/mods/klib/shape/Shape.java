package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.math.AAFBB;
import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface Shape extends CustomRegistryValue<ByteBuf, Shape> {
	UnitType<ByteBuf, Shape> EMPTY = UnitType.create("empty", EmptyShape.INSTANCE);
	UnitType<ByteBuf, Shape> UNIVERSE = UnitType.create("universe", UniverseShape.INSTANCE);
	CustomRegistry<ByteBuf, Shape> REGISTRY = CustomRegistry.create("shape", EMPTY);
	Codec<Ref<Shape>> CODEC = REGISTRY.codec();
	StreamCodec<ByteBuf, Ref<Shape>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<Shape>> DATA_TYPE = REGISTRY.dataType();

	static void builtInTypes(CustomRegistryTypeCollector<ByteBuf, Shape> registry) {
		registry.register(UNIVERSE);

		registry.register(CubeShape.TYPE);
		registry.register(CuboidShape.TYPE);
		registry.register(SphereShape.TYPE);
		registry.register(CylinderShape.TYPE);
		registry.register(LineShape.TYPE);
		registry.register(TransformedShape.TYPE);
		registry.register(VoxelShapeBox.TYPE);
		registry.register(QuadrilaterallyFacedConvexHexahedra.TYPE);

		registry.register(CubeShape.UNIT_CUBE);
		registry.register(CuboidShape.UNIT_SQUARE);
		registry.register(SphereShape.UNIT_SPHERE);
		registry.register(CylinderShape.UNIT_CYLINDER);
		registry.register(CylinderShape.UNIT_CIRCLE);
		registry.register(LineShape.UNIT_DOWN_LINE);
		registry.register(LineShape.UNIT_UP_LINE);
		registry.register(LineShape.UNIT_NORTH_LINE);
		registry.register(LineShape.UNIT_SOUTH_LINE);
		registry.register(LineShape.UNIT_WEST_LINE);
		registry.register(LineShape.UNIT_EAST_LINE);
	}

	@Override
	default CustomRegistry<ByteBuf, Shape> getRegistry() {
		return REGISTRY;
	}

	AAFBB box();

	default void buildLines(LineVertexCallback callback) {
		callback.cuboidLines(box());
	}

	default void buildOuterLines(LineVertexCallback callback) {
	}

	default void buildQuads(QuadVertexCallback callback) {
		callback.cuboidQuads(box());
	}

	default boolean contains(float x, float y, float z) {
		return box().contains(x, y, z);
	}

	default float clip(float x0, float y0, float z0, float x1, float y1, float z1) {
		return box().clip(x0, y0, z0, x1, y1, z1);
	}

	default boolean isVisible(double x, double y, double z, FrustumCheck frustum) {
		var box = box();
		return frustum.isVisible(x + box.minX(), y + box.minY(), z + box.minZ(), x + box.maxX(), y + box.maxY(), z + box.maxZ());
	}
}
