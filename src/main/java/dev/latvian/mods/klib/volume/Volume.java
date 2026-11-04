package dev.latvian.mods.klib.volume;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.math.AAIBB;
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

import java.util.List;

public interface Volume extends VolumeLike, CustomRegistryValue<ByteBuf, Volume> {
	UnitType<ByteBuf, Volume> EMPTY = UnitType.create("empty", EmptyVolume.INSTANCE);
	UnitType<ByteBuf, Volume> UNIVERSE = UnitType.create("universe", UniverseVolume.INSTANCE);

	CustomRegistry<ByteBuf, Volume> REGISTRY = CustomRegistry.<ByteBuf, Volume>builder("volume")
		.customCodec(AAIBB.CODEC.flatComapMap(BlockVolume::of, shape -> {
			if (shape instanceof BlockVolume b) {
				return DataResult.success(b.intBox());
			} else {
				return DataResult.error(() -> "Not an AAIBB");
			}
		}))
		.defaultType(EMPTY)
		.build();

	Codec<Ref<Volume>> CODEC = REGISTRY.codec();
	StreamCodec<ByteBuf, Ref<Volume>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<Volume>> DATA_TYPE = REGISTRY.dataType();

	static void builtInTypes(CustomRegistryTypeCollector<ByteBuf, Volume> registry) {
		registry.register(UNIVERSE);
		registry.register(JoinedVolume.TYPE);
		registry.register(BlockVolume.TYPE);
		registry.register(BoxVolume.TYPE);
		registry.register(ShapeVolume.TYPE);
		registry.register(RotatedBoxVolume.TYPE); // Will be deprecated when I figure out how to make it into a Shape
	}

	@Override
	default CustomRegistry<ByteBuf, Volume> getRegistry() {
		return REGISTRY;
	}

	default void buildLines(LineVertexCallback callback, Vec3 cameraPos) {
		var box = box();

		callback.cuboidLines(
			(float) (box.minX - cameraPos.x),
			(float) (box.minY - cameraPos.y),
			(float) (box.minZ - cameraPos.z),
			(float) (box.maxX - cameraPos.x),
			(float) (box.maxY - cameraPos.y),
			(float) (box.maxZ - cameraPos.z)
		);
	}

	default void buildOuterLines(LineVertexCallback callback, Vec3 cameraPos) {
	}

	default void buildQuads(QuadVertexCallback callback, Vec3 cameraPos) {
		var box = box();

		callback.cuboidQuads(
			(float) (box.minX - cameraPos.x),
			(float) (box.minY - cameraPos.y),
			(float) (box.minZ - cameraPos.z),
			(float) (box.maxX - cameraPos.x),
			(float) (box.maxY - cameraPos.y),
			(float) (box.maxZ - cameraPos.z)
		);
	}

	default void hit(HitBuilder hit) {
		if (contains(hit.from)) {
			return;
		}

		var result = AABB.clip(List.of(box()), hit.from, hit.to, BlockPos.ZERO);

		if (result != null && result.getType() == HitResult.Type.BLOCK) {
			hit.offer(result);
		}
	}

	default Volume move(double x, double y, double z) {
		var box = box();
		return new BoxVolume(box.move(x, y, z));
	}

	default Volume scale(double x, double y, double z) {
		var box = box();
		var c = box.getCenter();
		var sx = box.getXsize() * x / 2D;
		var sy = box.getYsize() * y / 2D;
		var sz = box.getZsize() * z / 2D;
		return new BoxVolume(new AABB(c.x - sx, c.y - sy, c.z - sz, c.x + sx, c.y + sy, c.z + sz));
	}
}
