package dev.latvian.mods.kmath;

import dev.latvian.mods.kmath.vertex.VertexCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;

public class QuadrilaterallyFacedConvexHexahedra implements Consumer<Consumer<Vector3f>>, Iterable<Face> {
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
}
