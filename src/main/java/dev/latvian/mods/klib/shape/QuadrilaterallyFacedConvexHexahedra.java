package dev.latvian.mods.klib.shape;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.JOMLCodecs;
import dev.latvian.mods.klib.codec.JOMLStreamCodecs;
import dev.latvian.mods.klib.math.Directions;
import dev.latvian.mods.klib.math.Face;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.math.Line3f;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;

public class QuadrilaterallyFacedConvexHexahedra implements Shape, Consumer<Consumer<Vector3f>>, Iterable<Face> {
	public static final MapCodec<QuadrilaterallyFacedConvexHexahedra> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		JOMLCodecs.VEC_3.fieldOf("nnn").forGetter(c -> c.nnn),
		JOMLCodecs.VEC_3.fieldOf("pnn").forGetter(c -> c.pnn),
		JOMLCodecs.VEC_3.fieldOf("pnp").forGetter(c -> c.pnp),
		JOMLCodecs.VEC_3.fieldOf("nnp").forGetter(c -> c.nnp),
		JOMLCodecs.VEC_3.fieldOf("npn").forGetter(c -> c.npn),
		JOMLCodecs.VEC_3.fieldOf("ppn").forGetter(c -> c.ppn),
		JOMLCodecs.VEC_3.fieldOf("ppp").forGetter(c -> c.ppp),
		JOMLCodecs.VEC_3.fieldOf("npp").forGetter(c -> c.npp)
	).apply(instance, (nnn, pnn, pnp, nnp, npn, ppn, ppp, npp) -> {
		var shape = new QuadrilaterallyFacedConvexHexahedra(0.5F);
		shape.nnn.set(nnn);
		shape.pnn.set(pnn);
		shape.pnp.set(pnp);
		shape.nnp.set(nnp);
		shape.npn.set(npn);
		shape.ppn.set(ppn);
		shape.ppp.set(ppp);
		shape.npp.set(npp);
		return shape;
	}));

	public static final StreamCodec<ByteBuf, QuadrilaterallyFacedConvexHexahedra> STREAM_CODEC = StreamCodec.composite(
		JOMLStreamCodecs.VEC_3, c -> c.nnn,
		JOMLStreamCodecs.VEC_3, c -> c.pnn,
		JOMLStreamCodecs.VEC_3, c -> c.pnp,
		JOMLStreamCodecs.VEC_3, c -> c.nnp,
		JOMLStreamCodecs.VEC_3, c -> c.npn,
		JOMLStreamCodecs.VEC_3, c -> c.ppn,
		JOMLStreamCodecs.VEC_3, c -> c.ppp,
		JOMLStreamCodecs.VEC_3, c -> c.npp
		, (nnn, pnn, pnp, nnp, npn, ppn, ppp, npp) -> {
			var shape = new QuadrilaterallyFacedConvexHexahedra(0.5F);
			shape.nnn.set(nnn);
			shape.pnn.set(pnn);
			shape.pnp.set(pnp);
			shape.nnp.set(nnp);
			shape.npn.set(npn);
			shape.ppn.set(ppn);
			shape.ppp.set(ppp);
			shape.npp.set(npp);
			return shape;
		});

	public static final ShapeType TYPE = new ShapeType("quadrilaterally_faced_convex_hexahedra", CODEC, STREAM_CODEC);

	public final float minX;
	public final float minY;
	public final float minZ;
	public final float maxX;
	public final float maxY;
	public final float maxZ;
	public final Vector3f nnn;
	public final Vector3f pnn;
	public final Vector3f pnp;
	public final Vector3f nnp;
	public final Vector3f npn;
	public final Vector3f ppn;
	public final Vector3f ppp;
	public final Vector3f npp;
	private final Face[] faces;
	private final Line3f[] edges;

	public QuadrilaterallyFacedConvexHexahedra(float mxs) {
		this(-mxs, -mxs, -mxs, mxs, mxs, mxs);
	}

	public QuadrilaterallyFacedConvexHexahedra(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;

		this.nnn = new Vector3f(minX, minY, minZ);
		this.pnn = new Vector3f(maxX, minY, minZ);
		this.pnp = new Vector3f(maxX, minY, maxZ);
		this.nnp = new Vector3f(minX, minY, maxZ);
		this.npn = new Vector3f(minX, maxY, minZ);
		this.ppn = new Vector3f(maxX, maxY, minZ);
		this.ppp = new Vector3f(maxX, maxY, maxZ);
		this.npp = new Vector3f(minX, maxY, maxZ);

		this.faces = new Face[]{
			new Face(nnp, nnn, pnn, pnp, Directions.ALL[0].getUnitVec3f()), // down
			new Face(npn, npp, ppp, ppn, Directions.ALL[1].getUnitVec3f()), // up
			new Face(ppn, pnn, nnn, npn, Directions.ALL[2].getUnitVec3f()), // north
			new Face(npp, nnp, pnp, ppp, Directions.ALL[3].getUnitVec3f()), // south
			new Face(npn, nnn, nnp, npp, Directions.ALL[4].getUnitVec3f()), // west
			new Face(ppp, pnp, pnn, ppn, Directions.ALL[5].getUnitVec3f()), // east
		};

		this.edges = new Line3f[]{
			new Line3f(nnn, pnn),
			new Line3f(pnn, pnp),
			new Line3f(pnp, nnp),
			new Line3f(nnp, nnn),

			new Line3f(npn, ppn),
			new Line3f(ppn, ppp),
			new Line3f(ppp, npp),
			new Line3f(npp, npn),

			new Line3f(nnn, npn),
			new Line3f(pnn, ppn),
			new Line3f(pnp, ppp),
			new Line3f(nnp, npp),
		};
	}

	public void identity() {
		nnn.set(minX, minY, minZ);
		pnn.set(maxX, minY, minZ);
		pnp.set(maxX, minY, maxZ);
		nnp.set(minX, minY, maxZ);
		npn.set(minX, maxY, minZ);
		ppn.set(maxX, maxY, minZ);
		ppp.set(maxX, maxY, maxZ);
		npp.set(minX, maxY, maxZ);
	}

	@Override
	public void accept(@Nullable Consumer<Vector3f> function) {
		identity();

		if (function != null) {
			forEachVertex(function);
		}
	}

	@Override
	@NotNull
	public Iterator<Face> iterator() {
		return Arrays.asList(faces).iterator();
	}

	public Face face(int face) {
		return faces[face];
	}

	public Line3f edge(int edge) {
		return edges[edge];
	}

	public Vector3f vertex(int vertex) {
		return switch (vertex) {
			case 0 -> nnn;
			case 1 -> pnn;
			case 2 -> pnp;
			case 3 -> nnp;
			case 4 -> npn;
			case 5 -> ppn;
			case 6 -> ppp;
			case 7 -> npp;
			default -> throw new IndexOutOfBoundsException("Vertex index must be between 0 and 7, got: " + vertex);
		};
	}

	public void forEachVertex(Consumer<Vector3f> callback) {
		callback.accept(nnn);
		callback.accept(pnn);
		callback.accept(pnp);
		callback.accept(nnp);
		callback.accept(npn);
		callback.accept(ppn);
		callback.accept(ppp);
		callback.accept(npp);
	}

	public void forEachFace(VertexCallback callback) {
		for (var face : faces) {
			face.forEachVertex(callback);
		}
	}

	public void forEachEdge(Consumer<Line3f> callback) {
		for (var edge : edges) {
			callback.accept(edge);
		}
	}

	public Vector3f getCenter() {
		return new Vector3f(
			(nnn.x + pnn.x + pnp.x + nnp.x + npn.x + ppn.x + ppp.x + npp.x) / 8F,
			(nnn.y + pnn.y + pnp.y + nnp.y + npn.y + ppn.y + ppp.y + npp.y) / 8F,
			(nnn.z + pnn.z + pnp.z + nnp.z + npn.z + ppn.z + ppp.z + npp.z) / 8F
		);
	}

	@Override
	public ShapeType type() {
		return TYPE;
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
		if (x == 0F && y == 0F && z == 0F) {
			forEachEdge(callback::line);
		} else {
			forEachEdge(callback.withTransformedPositions(new Matrix4f().translate(x, y, z))::line);
		}
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		if (x == 0F && y == 0F && z == 0F) {
			forEachFace(callback);
		} else {
			forEachFace(callback.withTransformedPositions(new Matrix4f().translate(x, y, z)));
		}
	}

	@Override
	public boolean contains(Vector3fc p) {
		var ab = new Vector3f();
		var ac = new Vector3f();
		var normal = new Vector3f();
		var ap = new Vector3f();

		return faceContains(nnn, pnn, pnp, p, ab, ac, normal, ap) // bottom
			&& faceContains(nnn, pnp, nnp, p, ab, ac, normal, ap)

			&& faceContains(npn, ppn, ppp, p, ab, ac, normal, ap) // top
			&& faceContains(npn, ppp, npp, p, ab, ac, normal, ap)

			&& faceContains(nnn, npn, ppn, p, ab, ac, normal, ap) // front
			&& faceContains(nnn, ppn, pnn, p, ab, ac, normal, ap)

			&& faceContains(pnn, ppn, ppp, p, ab, ac, normal, ap) // right
			&& faceContains(pnn, ppp, pnp, p, ab, ac, normal, ap)

			&& faceContains(pnp, ppp, npp, p, ab, ac, normal, ap) // back
			&& faceContains(pnp, npp, nnp, p, ab, ac, normal, ap)

			&& faceContains(nnp, npp, npn, p, ab, ac, normal, ap) // left
			&& faceContains(nnp, npn, nnn, p, ab, ac, normal, ap);
	}

	private boolean faceContains(Vector3f a, Vector3f b, Vector3f c, Vector3fc point, Vector3f ab, Vector3f ac, Vector3f normal, Vector3f ap) {
		b.sub(a, ab);
		c.sub(a, ac);
		ab.cross(ac, normal);
		point.sub(a, ap);
		return normal.dot(ap) <= 0;
	}

	@Override
	public String toString() {
		return "QFCH[" +
			"nnn=" + KMath.format(nnn) +
			", pnn=" + KMath.format(pnn) +
			", pnp=" + KMath.format(pnp) +
			", nnp=" + KMath.format(nnp) +
			", npn=" + KMath.format(npn) +
			", ppn=" + KMath.format(ppn) +
			", ppp=" + KMath.format(ppp) +
			", npp=" + KMath.format(npp) +
			']';
	}
}
