package dev.latvian.mods.kmath;

import dev.latvian.mods.kmath.render.BoxBuilder;
import dev.latvian.mods.kmath.vertex.VertexCallback;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public record VoxelShapeBox(List<Line> edges, List<AABB> boxes) {
	public static final VoxelShapeBox EMPTY = new VoxelShapeBox(List.of(), List.of());
	public static final VoxelShapeBox FULL = of(AABBs.FULL);
	public static final VoxelShapeBox INFINITE = of(AABB.INFINITE);
	public static final VoxelShapeBox CENTERED = of(AABBs.CENTERED);
	public static final VoxelShapeBox CENTERED_X_PLANE = of(AABBs.CENTERED_X_PLANE);
	public static final VoxelShapeBox CENTERED_Y_PLANE = of(AABBs.CENTERED_Y_PLANE);
	public static final VoxelShapeBox CENTERED_Z_PLANE = of(AABBs.CENTERED_Z_PLANE);

	public static VoxelShapeBox of(VoxelShape shape) {
		if (shape.isEmpty()) {
			return EMPTY;
		} else if (shape == Shapes.block()) {
			return FULL;
		}

		var boxes = new ArrayList<AABB>();
		shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ)));
		var edges = new ArrayList<Line>();
		shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> edges.add(new Line(new Vec3(minX, minY, minZ), new Vec3(maxX, maxY, maxZ))));
		return edges.isEmpty() && boxes.isEmpty() ? EMPTY : new VoxelShapeBox(List.copyOf(edges), List.copyOf(boxes));
	}

	private static void edge(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, List<Line> edges) {
		if (minX != maxX || minY != maxY || minZ != maxZ) {
			edges.add(new Line(new Vec3(minX, minY, minZ), new Vec3(maxX, maxY, maxZ)));
		}
	}

	public static VoxelShapeBox of(AABB box) {
		var edges = new ArrayList<Line>(12);
		double minX = box.minX;
		double minY = box.minY;
		double minZ = box.minZ;
		double maxX = box.maxX;
		double maxY = box.maxY;
		double maxZ = box.maxZ;
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
		return new VoxelShapeBox(List.copyOf(edges), List.of(box));
	}

	public void buildQuads(Vec3 offset, VertexCallback callback) {
		for (var box : boxes) {
			float minX = (float) (box.minX + offset.x);
			float minY = (float) (box.minY + offset.y);
			float minZ = (float) (box.minZ + offset.z);
			float maxX = (float) (box.maxX + offset.x);
			float maxY = (float) (box.maxY + offset.y);
			float maxZ = (float) (box.maxZ + offset.z);
			BoxBuilder.quads(minX, minY, minZ, maxX, maxY, maxZ, callback);
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

			callback.acceptPos(minX, minY, minZ);
			callback.acceptPos(maxX, maxY, maxZ);
		}
	}
}
