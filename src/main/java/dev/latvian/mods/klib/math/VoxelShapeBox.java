package dev.latvian.mods.klib.math;

import dev.latvian.mods.klib.shape.CuboidBuilder;
import dev.latvian.mods.klib.vertex.VertexCallback;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public record VoxelShapeBox(List<Line> edges, List<AABB> boxes, boolean singleBox) {
	public static final VoxelShapeBox EMPTY = new VoxelShapeBox(List.of(), List.of(), false);
	public static final VoxelShapeBox FULL = of(AABBs.FULL);
	public static final VoxelShapeBox FULL_16 = of(AABBs.FULL_16);
	public static final VoxelShapeBox INFINITE = of(AABB.INFINITE);
	public static final VoxelShapeBox CENTERED = of(AABBs.CENTERED);
	public static final VoxelShapeBox CENTERED_X_PLANE = of(AABBs.CENTERED_X_PLANE);
	public static final VoxelShapeBox CENTERED_Y_PLANE = of(AABBs.CENTERED_Y_PLANE);
	public static final VoxelShapeBox CENTERED_Z_PLANE = of(AABBs.CENTERED_Z_PLANE);
	public static final VoxelShapeBox CENTERED_X_AXIS = of(AABBs.CENTERED_X_AXIS);
	public static final VoxelShapeBox CENTERED_Y_AXIS = of(AABBs.CENTERED_Y_AXIS);
	public static final VoxelShapeBox CENTERED_Z_AXIS = of(AABBs.CENTERED_Z_AXIS);

	public static VoxelShapeBox of(VoxelShape shape) {
		if (shape.isEmpty()) {
			return EMPTY;
		} else if (shape == Shapes.block()) {
			return FULL;
		}

		var boxes = new ArrayList<AABB>(1);
		shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ)));

		if (boxes.size() == 1) {
			return of(boxes.getFirst());
		}

		var edges = new ArrayList<Line>(12);
		shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> edges.add(new Line(new Vec3(minX, minY, minZ), new Vec3(maxX, maxY, maxZ))));
		return edges.isEmpty() && boxes.isEmpty() ? EMPTY : new VoxelShapeBox(List.copyOf(edges), List.copyOf(boxes), boxes.size() == 1);
	}

	private static void edge(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, List<Line> edges) {
		edges.add(new Line(new Vec3(minX, minY, minZ), new Vec3(maxX, maxY, maxZ)));
	}

	public static VoxelShapeBox of(AABB box) {
		double minX = box.minX;
		double minY = box.minY;
		double minZ = box.minZ;
		double maxX = box.maxX;
		double maxY = box.maxY;
		double maxZ = box.maxZ;

		if (minX == maxX && minY == maxY && minZ == maxZ) {
			return EMPTY;
		} else if (minY == maxY && minZ == maxZ) {
			var edges = new ArrayList<Line>(1);
			edge(minX, minY, minZ, maxX, minY, minZ, edges);
			return new VoxelShapeBox(List.copyOf(edges), List.of(box), true);
		} else if (minX == maxX && minZ == maxZ) {
			var edges = new ArrayList<Line>(1);
			edge(minX, minY, minZ, minX, maxY, minZ, edges);
			return new VoxelShapeBox(List.copyOf(edges), List.of(box), true);
		} else if (minX == maxX && minY == maxY) {
			var edges = new ArrayList<Line>(1);
			edge(minX, minY, minZ, minX, minY, maxZ, edges);
			return new VoxelShapeBox(List.copyOf(edges), List.of(box), true);
		} else if (minX == maxX) {
			var edges = new ArrayList<Line>(4);
			edge(minX, minY, minZ, minX, maxY, minZ, edges);
			edge(minX, minY, minZ, minX, minY, maxZ, edges);
			edge(minX, maxY, minZ, minX, maxY, maxZ, edges);
			edge(minX, maxY, maxZ, minX, minY, maxZ, edges);
			return new VoxelShapeBox(List.copyOf(edges), List.of(box), true);
		} else if (minY == maxY) {
			var edges = new ArrayList<Line>(4);
			edge(minX, minY, minZ, maxX, minY, minZ, edges);
			edge(minX, minY, minZ, minX, minY, maxZ, edges);
			edge(minX, minY, maxZ, maxX, minY, maxZ, edges);
			edge(maxX, minY, maxZ, maxX, minY, minZ, edges);
			return new VoxelShapeBox(List.copyOf(edges), List.of(box), true);
		} else if (minZ == maxZ) {
			var edges = new ArrayList<Line>(4);
			edge(minX, minY, minZ, maxX, minY, minZ, edges);
			edge(minX, minY, minZ, minX, maxY, minZ, edges);
			edge(maxX, minY, minZ, maxX, maxY, minZ, edges);
			edge(maxX, maxY, minZ, minX, maxY, minZ, edges);
			return new VoxelShapeBox(List.copyOf(edges), List.of(box), true);
		} else {
			var edges = new ArrayList<Line>(12);
			edge(minX, minY, minZ, maxX, minY, minZ, edges);
			edge(minX, minY, minZ, minX, maxY, minZ, edges);
			edge(minX, minY, minZ, minX, minY, maxZ, edges);
			edge(maxX, minY, minZ, maxX, maxY, minZ, edges);
			edge(maxX, maxY, minZ, minX, maxY, minZ, edges);
			edge(minX, maxY, minZ, minX, maxY, maxZ, edges);
			edge(minX, maxY, maxZ, minX, minY, maxZ, edges);
			edge(minX, minY, maxZ, maxX, minY, maxZ, edges);
			edge(maxX, minY, maxZ, maxX, minY, minZ, edges);
			edge(minX, maxY, maxZ, maxX, maxY, maxZ, edges);
			edge(maxX, minY, maxZ, maxX, maxY, maxZ, edges);
			edge(maxX, maxY, minZ, maxX, maxY, maxZ, edges);
			return new VoxelShapeBox(List.copyOf(edges), List.of(box), true);
		}
	}

	public void buildQuads(Vec3 offset, VertexCallback callback) {
		for (var box : boxes) {
			float minX = (float) (box.minX + offset.x);
			float minY = (float) (box.minY + offset.y);
			float minZ = (float) (box.minZ + offset.z);
			float maxX = (float) (box.maxX + offset.x);
			float maxY = (float) (box.maxY + offset.y);
			float maxZ = (float) (box.maxZ + offset.z);
			CuboidBuilder.quads(minX, minY, minZ, maxX, maxY, maxZ, callback);
		}
	}

	public void buildLines(Vec3 offset, VertexCallback callback) {
		for (var edge : edges) {
			float minX = (float) (edge.start().x + offset.x);
			float minY = (float) (edge.start().y + offset.y);
			float minZ = (float) (edge.start().z + offset.z);
			float maxX = (float) (edge.end().x + offset.x);
			float maxY = (float) (edge.end().y + offset.y);
			float maxZ = (float) (edge.end().z + offset.z);
			callback.line(minX, minY, minZ, maxX, maxY, maxZ);
		}
	}

	public VoxelShapeBox move(double x, double y, double z) {
		if (x == 0D && y == 0D && z == 0D) {
			return this;
		} else if (singleBox) {
			var b = boxes.getFirst();
			return of(new AABB(b.minX + x, b.minY + y, b.minZ + z, b.maxX + x, b.maxY + y, b.maxZ + z));
		} else {
			var movedBoxes = new ArrayList<AABB>(boxes.size());
			var movedEdges = new ArrayList<Line>(edges.size());

			for (var b : boxes) {
				movedBoxes.add(new AABB(
					b.minX + x,
					b.minY + y,
					b.minZ + z,
					b.maxX + x,
					b.maxY + y,
					b.maxZ + z
				));
			}

			for (var e : edges) {
				movedEdges.add(new Line(
					new Vec3(e.start().x + x, e.start().y + y, e.start().z + z),
					new Vec3(e.end().x + x, e.end().y + y, e.end().z + z)
				));
			}

			return new VoxelShapeBox(List.copyOf(movedEdges), List.copyOf(movedBoxes), false);
		}
	}

	public VoxelShapeBox scale(double sx, double sy, double sz) {
		if (sx == 1D && sy == 1D && sz == 1D) {
			return this;
		} else if (singleBox) {
			var b = boxes.getFirst();
			return of(new AABB(b.minX * sx, b.minY * sy, b.minZ * sz, b.maxX * sx, b.maxY * sy, b.maxZ * sz));
		}

		var scaledBoxes = new ArrayList<AABB>(boxes.size());
		var scaledEdges = new ArrayList<Line>(edges.size());

		for (var b : boxes) {
			scaledBoxes.add(new AABB(
				b.minX * sx,
				b.minY * sy,
				b.minZ * sz,
				b.maxX * sx,
				b.maxY * sy,
				b.maxZ * sz
			));
		}

		for (var e : edges) {
			scaledEdges.add(new Line(
				new Vec3(e.start().x * sx, e.start().y * sy, e.start().z * sz),
				new Vec3(e.end().x * sx, e.end().y * sy, e.end().z * sz)
			));
		}

		return new VoxelShapeBox(List.copyOf(scaledEdges), List.copyOf(scaledBoxes), false);
	}
}
