package dev.latvian.mods.klib.volume;

import dev.latvian.mods.klib.math.AABBs;
import dev.latvian.mods.klib.math.HitBuilder;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Predicate;
import java.util.stream.Stream;

public enum UniverseVolume implements Volume {
	INSTANCE;

	@Override
	public UnitType<ByteBuf, Volume> type() {
		return UNIVERSE;
	}

	@Override
	public AABB box() {
		return AABBs.INFINITE;
	}

	@Override
	public Vec3 centerPos() {
		return Vec3.ZERO;
	}

	@Override
	public Volume move(double x, double y, double z) {
		return this;
	}

	@Override
	public Volume scale(double x, double y, double z) {
		return this;
	}

	@Override
	public void buildLines(LineVertexCallback callback, Vec3 cameraPos) {
	}

	@Override
	public void buildQuads(QuadVertexCallback callback, Vec3 cameraPos) {
	}

	@Override
	public void hit(HitBuilder hit) {
	}

	@Override
	public boolean contains(double x, double y, double z) {
		return true;
	}

	@Override
	public boolean intersects(AABB box) {
		return true;
	}

	@Override
	public Stream<BlockPos> getBlocks() {
		return Stream.empty();
	}

	@Override
	public VoxelShape createVoxelShape() {
		return Shapes.empty();
	}

	@Override
	public VoxelShape createBlockRenderingShape(Predicate<BlockPos> predicate) {
		return Shapes.empty();
	}

	@Override
	public double closestDistanceTo(Vec3 pos) {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public void collectChunkPositions(LongSet chunks) {
	}

	@Override
	public LongSet collectChunkPositions() {
		return LongSets.emptySet();
	}
}
