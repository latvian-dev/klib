package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3fc;

public interface Shape extends CustomRegistryValue<ByteBuf, Shape> {
	UnitType<ByteBuf, Shape> EMPTY = UnitType.create("empty", EmptyShape.INSTANCE);
	CustomRegistry<ByteBuf, Shape> REGISTRY = CustomRegistry.create("shape", EMPTY);
	Codec<Ref<Shape>> CODEC = REGISTRY.codec();
	StreamCodec<ByteBuf, Ref<Shape>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<Shape>> DATA_TYPE = REGISTRY.dataType();

	static void builtInTypes(CustomRegistryTypeCollector<ByteBuf, Shape> registry) {
		registry.register(CubeShape.TYPE);
		registry.register(CuboidShape.TYPE);
		registry.register(CircleShape.TYPE);
		registry.register(SphereShape.TYPE);
		registry.register(CylinderShape.TYPE);
		registry.register(LineShape.TYPE);
		registry.register(QuadrilaterallyFacedConvexHexahedra.TYPE);
		registry.register(RotatedShape.TYPE);
		registry.register(VoxelShapeBox.TYPE);

		registry.register(CubeShape.UNIT_CUBE);
		registry.register(CuboidShape.UNIT_SQUARE);
		registry.register(SphereShape.UNIT_SPHERE);
		registry.register(CylinderShape.UNIT_CYLINDER);
		registry.register(CircleShape.UNIT_CIRCLE);
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

	void buildLines(float x, float y, float z, VertexCallback callback);

	void buildQuads(float x, float y, float z, VertexCallback callback);

	default boolean contains(Vector3fc p) {
		return false;
	}

	default boolean isVisible(double x, double y, double z, FrustumCheck frustum) {
		return true;
	}
}
