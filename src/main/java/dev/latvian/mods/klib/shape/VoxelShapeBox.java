package dev.latvian.mods.klib.shape;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.math.AAFBB;
import dev.latvian.mods.klib.math.Line3f;
import dev.latvian.mods.klib.math.Vec3f;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public record VoxelShapeBox(List<Line3f> edges, List<AAFBB> boxes, boolean singleBox, AAFBB box) implements Shape {
	public static final VoxelShapeBox EMPTY = new VoxelShapeBox(List.of(), List.of(), false);
	public static final VoxelShapeBox FULL = of(AAFBB.FULL);
	public static final VoxelShapeBox FULL_16 = of(AAFBB.FULL_16);
	public static final VoxelShapeBox INFINITE = of(AAFBB.INFINITE);
	public static final VoxelShapeBox CENTERED = of(AAFBB.CENTERED);
	public static final VoxelShapeBox CENTERED_X_PLANE = of(AAFBB.CENTERED_X_PLANE);
	public static final VoxelShapeBox CENTERED_Y_PLANE = of(AAFBB.CENTERED_Y_PLANE);
	public static final VoxelShapeBox CENTERED_Z_PLANE = of(AAFBB.CENTERED_Z_PLANE);
	public static final VoxelShapeBox CENTERED_X_AXIS = of(AAFBB.CENTERED_X_AXIS);
	public static final VoxelShapeBox CENTERED_Y_AXIS = of(AAFBB.CENTERED_Y_AXIS);
	public static final VoxelShapeBox CENTERED_Z_AXIS = of(AAFBB.CENTERED_Z_AXIS);

	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"voxel_shape",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Line3f.CODEC.listOf().fieldOf("edges").forGetter(VoxelShapeBox::edges),
			AAFBB.CODEC.listOf().fieldOf("boxes").forGetter(VoxelShapeBox::boxes)
		).apply(instance, (edges, boxes) -> new VoxelShapeBox(edges, boxes, boxes.size() == 1))),
		CompositeStreamCodec.of(
			KLibStreamCodecs.listOf(Line3f.STREAM_CODEC), VoxelShapeBox::edges,
			KLibStreamCodecs.listOf(AAFBB.STREAM_CODEC), VoxelShapeBox::boxes,
			(edges, boxes) -> new VoxelShapeBox(edges, boxes, boxes.size() == 1)
		)
	);

	public static VoxelShapeBox of(VoxelShape shape) {
		if (shape.isEmpty()) {
			return EMPTY;
		} else if (shape == Shapes.block()) {
			return FULL;
		}

		var boxes = new ArrayList<AAFBB>(1);
		shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> boxes.add(new AAFBB((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ)));

		if (boxes.size() == 1) {
			return of(boxes.getFirst());
		}

		var edges = new ArrayList<Line3f>(12);
		shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> edges.add(Line3f.of((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ)));
		return edges.isEmpty() && boxes.isEmpty() ? EMPTY : new VoxelShapeBox(List.copyOf(edges), List.copyOf(boxes), boxes.size() == 1);
	}

	private static void edge(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, List<Line3f> edges) {
		edges.add(Line3f.of(minX, minY, minZ, maxX, maxY, maxZ));
	}

	public static VoxelShapeBox of(AAFBB box) {
		float minX = box.minX();
		float minY = box.minY();
		float minZ = box.minZ();
		float maxX = box.maxX();
		float maxY = box.maxY();
		float maxZ = box.maxZ();

		if (minX == maxX && minY == maxY && minZ == maxZ) {
			return EMPTY;
		} else if (minY == maxY && minZ == maxZ) {
			var edges = new ArrayList<Line3f>(1);
			edge(minX, minY, minZ, maxX, minY, minZ, edges);
			return new VoxelShapeBox(List.copyOf(edges), List.of(box), true);
		} else if (minX == maxX && minZ == maxZ) {
			var edges = new ArrayList<Line3f>(1);
			edge(minX, minY, minZ, minX, maxY, minZ, edges);
			return new VoxelShapeBox(List.copyOf(edges), List.of(box), true);
		} else if (minX == maxX && minY == maxY) {
			var edges = new ArrayList<Line3f>(1);
			edge(minX, minY, minZ, minX, minY, maxZ, edges);
			return new VoxelShapeBox(List.copyOf(edges), List.of(box), true);
		} else if (minX == maxX) {
			var edges = new ArrayList<Line3f>(4);
			edge(minX, minY, minZ, minX, maxY, minZ, edges);
			edge(minX, minY, minZ, minX, minY, maxZ, edges);
			edge(minX, maxY, minZ, minX, maxY, maxZ, edges);
			edge(minX, maxY, maxZ, minX, minY, maxZ, edges);
			return new VoxelShapeBox(List.copyOf(edges), List.of(box), true);
		} else if (minY == maxY) {
			var edges = new ArrayList<Line3f>(4);
			edge(minX, minY, minZ, maxX, minY, minZ, edges);
			edge(minX, minY, minZ, minX, minY, maxZ, edges);
			edge(minX, minY, maxZ, maxX, minY, maxZ, edges);
			edge(maxX, minY, maxZ, maxX, minY, minZ, edges);
			return new VoxelShapeBox(List.copyOf(edges), List.of(box), true);
		} else if (minZ == maxZ) {
			var edges = new ArrayList<Line3f>(4);
			edge(minX, minY, minZ, maxX, minY, minZ, edges);
			edge(minX, minY, minZ, minX, maxY, minZ, edges);
			edge(maxX, minY, minZ, maxX, maxY, minZ, edges);
			edge(maxX, maxY, minZ, minX, maxY, minZ, edges);
			return new VoxelShapeBox(List.copyOf(edges), List.of(box), true);
		} else {
			var edges = new ArrayList<Line3f>(12);
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

	public VoxelShapeBox(List<Line3f> edges, List<AAFBB> boxes, boolean singleBox) {
		this(edges, boxes, singleBox, AAFBB.max(boxes));
	}

	@Override
	public DynamicType<ByteBuf, Shape> type() {
		return TYPE;
	}

	public VoxelShapeBox move(float x, float y, float z) {
		if (x == 0F && y == 0F && z == 0F) {
			return this;
		} else if (singleBox) {
			var b = boxes.getFirst();
			return of(new AAFBB(b.minX() + x, b.minY() + y, b.minZ() + z, b.maxX() + x, b.maxY() + y, b.maxZ() + z));
		} else {
			var movedBoxes = new ArrayList<AAFBB>(boxes.size());
			var movedEdges = new ArrayList<Line3f>(edges.size());

			for (var b : boxes) {
				movedBoxes.add(new AAFBB(
					b.minX() + x,
					b.minY() + y,
					b.minZ() + z,
					b.maxX() + x,
					b.maxY() + y,
					b.maxZ() + z
				));
			}

			for (var e : edges) {
				movedEdges.add(new Line3f(
					Vec3f.of(e.start().x() + x, e.start().y() + y, e.start().z() + z),
					Vec3f.of(e.end().x() + x, e.end().y() + y, e.end().z() + z)
				));
			}

			return new VoxelShapeBox(List.copyOf(movedEdges), List.copyOf(movedBoxes), false);
		}
	}

	public VoxelShapeBox scale(float sx, float sy, float sz) {
		if (sx == 1F && sy == 1F && sz == 1F) {
			return this;
		} else if (singleBox) {
			var b = boxes.getFirst();
			return of(new AAFBB(b.minX() * sx, b.minY() * sy, b.minZ() * sz, b.maxX() * sx, b.maxY() * sy, b.maxZ() * sz));
		}

		var scaledBoxes = new ArrayList<AAFBB>(boxes.size());
		var scaledEdges = new ArrayList<Line3f>(edges.size());

		for (var b : boxes) {
			scaledBoxes.add(new AAFBB(
				b.minX() * sx,
				b.minY() * sy,
				b.minZ() * sz,
				b.maxX() * sx,
				b.maxY() * sy,
				b.maxZ() * sz
			));
		}

		for (var e : edges) {
			scaledEdges.add(new Line3f(
				Vec3f.of(e.start().x() * sx, e.start().y() * sy, e.start().z() * sz),
				Vec3f.of(e.end().x() * sx, e.end().y() * sy, e.end().z() * sz)
			));
		}

		return new VoxelShapeBox(List.copyOf(scaledEdges), List.copyOf(scaledBoxes), false);
	}

	@Override
	public void buildLines(LineVertexCallback callback) {
		for (var edge : edges) {
			callback.line(edge);
		}
	}

	@Override
	public void buildOuterLines(LineVertexCallback callback) {
		if (boxes.size() > 1) {
			callback.cuboidLines(box);
		}
	}

	@Override
	public void buildQuads(QuadVertexCallback callback) {
		for (var box : boxes) {
			callback.cuboidQuads(box);
		}
	}

	@Override
	public boolean contains(float x, float y, float z) {
		for (var box : boxes) {
			if (box.contains(x, y, z)) {
				return true;
			}
		}

		return false;
	}
}
