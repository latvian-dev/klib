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
	public static final VoxelShapeBox FULL = of(Shapes.block());
	public static final VoxelShapeBox FULL_CENTERED = of(Shapes.create(-0.5D, -0.5D, -0.5D, 0.5D, 0.5D, 0.5D));
	public static final VoxelShapeBox FULL_CENTERED_X = of(Shapes.create(0D, -0.5D, -0.5D, 0D, 0.5D, 0.5D));
	public static final VoxelShapeBox FULL_CENTERED_Y = of(Shapes.create(-0.5D, 0D, -0.5D, 0.5D, 0D, 0.5D));
	public static final VoxelShapeBox FULL_CENTERED_Z = of(Shapes.create(-0.5D, -0.5D, 0D, 0.5D, 0.5D, 0D));

	public static VoxelShapeBox of(VoxelShape shape) {
		if (shape.isEmpty()) {
			return EMPTY;
		}

		var edges = new ArrayList<Line>();
		var boxes = new ArrayList<AABB>();
		shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ)));
		shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> edges.add(new Line(new Vec3(minX, minY, minZ), new Vec3(maxX, maxY, maxZ))));
		return edges.isEmpty() && boxes.isEmpty() ? EMPTY : new VoxelShapeBox(List.copyOf(edges), List.copyOf(boxes));
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
