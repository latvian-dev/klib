package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.math.AAFBB;
import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.math.HitBuilder;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;

import java.util.List;

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

	default void buildLines(Vector3fc offset, LineVertexCallback callback) {
		callback.cuboidLines(box().move(offset));
	}

	default void buildOuterLines(Vector3fc offset, LineVertexCallback callback) {
	}

	default void buildQuads(Vector3fc offset, QuadVertexCallback callback) {
		callback.cuboidQuads(box().move(offset));
	}

	default Shape scale(double x, double y, double z) {
		return this;
	}

	default boolean isVisible(double x, double y, double z, FrustumCheck frustum) {
		var box = box();
		return frustum.isVisible(x + box.minX(), y + box.minY(), z + box.minZ(), x + box.maxX(), y + box.maxY(), z + box.maxZ());
	}

	default boolean contains(float x, float y, float z) {
		return box().contains(x, y, z);
	}

	default boolean hit(Vec3 pos, HitBuilder hit) {
		var aabb = box().aabb(pos);

		if (aabb.contains(hit.from)) {
			return false;
		}

		var result = AABB.clip(List.of(aabb), hit.from, hit.to, BlockPos.ZERO);

		if (result != null && result.getType() == HitResult.Type.BLOCK) {
			hit.offer(result);
			return true;
		}

		return false;
	}

	default boolean intersects(AAFBB box) {
		return box().intersects(box);
	}

	default double closestDistanceTo(float x, float y, float z) {
		var box = box();
		return box.contains(x, y, z) ? 0F : Math.sqrt(box.distanceToSqr(x, y, z));
	}
}
