package dev.latvian.mods.klib.volume;

import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.math.AAIBB;
import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.math.HitBuilder;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public record JoinedVolume(List<Ref<Volume>> volumes, AABB box, AAIBB intBox) implements Volume {
	public static final DynamicType<ByteBuf, Volume> TYPE = DynamicType.create(
		"joined",
		"volumes",
		Volume.CODEC.listOf(),
		KLibStreamCodecs.listOf(Volume.STREAM_CODEC),
		JoinedVolume::create,
		JoinedVolume::volumes
	);

	public static JoinedVolume create(List<Ref<Volume>> volumes) {
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double minZ = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double maxZ = Double.NEGATIVE_INFINITY;
		int iminX = Integer.MAX_VALUE;
		int iminY = Integer.MAX_VALUE;
		int iminZ = Integer.MAX_VALUE;
		int imaxX = Integer.MIN_VALUE;
		int imaxY = Integer.MIN_VALUE;
		int imaxZ = Integer.MIN_VALUE;

		for (var ref : volumes) {
			var volume = ref.value();
			var box = volume.box();
			minX = Math.min(minX, box.minX);
			minY = Math.min(minY, box.minY);
			minZ = Math.min(minZ, box.minZ);
			maxX = Math.max(maxX, box.maxX);
			maxY = Math.max(maxY, box.maxY);
			maxZ = Math.max(maxZ, box.maxZ);
			var ibox = volume.intBox();
			iminX = Math.min(iminX, ibox.minX());
			iminY = Math.min(iminY, ibox.minY());
			iminZ = Math.min(iminZ, ibox.minZ());
			imaxX = Math.max(imaxX, ibox.maxX());
			imaxY = Math.max(imaxY, ibox.maxY());
			imaxZ = Math.max(imaxZ, ibox.maxZ());
		}

		return new JoinedVolume(volumes, new AABB(minX, minY, minZ, maxX, maxY, maxZ), new AAIBB(iminX, iminY, iminZ, imaxX, imaxY, imaxZ));
	}

	@Override
	public DynamicType<ByteBuf, Volume> type() {
		return TYPE;
	}

	@Override
	public boolean hit(HitBuilder hit) {
		boolean anyHit = false;

		for (var volume : volumes) {
			if (volume.value().hit(hit)) {
				anyHit = true;
			}
		}

		return anyHit;
	}

	@Override
	public boolean contains(double x, double y, double z) {
		if (box != null && !box.contains(x, y, z)) {
			return false;
		}

		for (var volume : volumes) {
			if (volume.value().contains(x, y, z)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean contains(int x, int y, int z) {
		if (intBox != null && !intBox.contains(x, y, z)) {
			return false;
		}

		for (var volume : volumes) {
			if (volume.value().contains(x, y, z)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean intersects(AABB box) {
		if (box != null && !box.intersects(box)) {
			return false;
		}

		for (var volume : volumes) {
			if (volume.value().intersects(box)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Stream<BlockPos> getBlocks() {
		var stream = Stream.<BlockPos>empty();

		for (var volume : volumes) {
			stream = Stream.concat(stream, volume.value().getBlocks());
		}

		return stream;
	}

	@Override
	public VoxelShape createVoxelShape() {
		if (volumes.isEmpty()) {
			return Shapes.empty();
		}

		var shape = volumes.getFirst().value().createVoxelShape();

		for (int i = 1; i < volumes.size(); i++) {
			shape = Shapes.or(shape, volumes.get(i).value().createVoxelShape());
		}

		return shape;
	}

	@Override
	public VoxelShape createBlockRenderingShape(Predicate<BlockPos> predicate) {
		if (volumes.isEmpty()) {
			return Shapes.empty();
		}

		var shape = volumes.getFirst().value().createBlockRenderingShape(predicate);

		for (int i = 1; i < volumes.size(); i++) {
			shape = Shapes.or(shape, volumes.get(i).value().createBlockRenderingShape(predicate));
		}

		return shape;
	}

	@Override
	public double closestDistanceTo(Vec3 pos) {
		var dist = Double.POSITIVE_INFINITY;

		for (var shape : volumes) {
			dist = Math.min(dist, shape.value().closestDistanceTo(pos));

			if (dist <= 0D) {
				return 0D;
			}
		}

		return dist;
	}

	@Override
	public Volume move(double x, double y, double z) {
		var shapes = new ArrayList<Ref<Volume>>(volumes.size());

		for (var shape : volumes) {
			shapes.add(shape.value().move(x, y, z).ref());
		}

		var moved = box.move(x, y, z);
		return new JoinedVolume(shapes, moved, new AAIBB(Mth.floor(moved.minX), Mth.floor(moved.minY), Mth.floor(moved.minZ), Mth.ceil(moved.maxX), Mth.ceil(moved.maxY), Mth.ceil(moved.maxZ)));
	}

	@Override
	public Volume scale(double x, double y, double z) {
		return this;
	}

	@Override
	public boolean isVisible(FrustumCheck frustum) {
		for (var volume : volumes) {
			if (volume.value().isVisible(frustum)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void buildLines(LineVertexCallback callback, Vec3 cameraPos) {
		for (var volume : volumes) {
			volume.value().buildLines(callback, cameraPos);
		}
	}

	@Override
	public void buildOuterLines(LineVertexCallback callback, Vec3 cameraPos) {
		for (var volume : volumes) {
			volume.value().buildOuterLines(callback, cameraPos);
		}
	}

	@Override
	public void buildQuads(QuadVertexCallback callback, Vec3 cameraPos) {
		for (var volume : volumes) {
			volume.value().buildQuads(callback, cameraPos);
		}
	}
}
