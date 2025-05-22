package dev.latvian.mods.kmath;

import dev.latvian.mods.kmath.vertex.VertexCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;

public class QuadrilaterallyFacedConvexHexahedra implements Consumer<Consumer<Vector3f>>, Iterable<Face> {
	public static final QuadrilaterallyFacedConvexHexahedra CUBE = new QuadrilaterallyFacedConvexHexahedra(0.5F) {
		@Override
		public void accept(@Nullable Consumer<Vector3f> function) {
		}
	};

	public final float mxs;
	public final float mns;
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
		this.mxs = mxs;
		this.mns = -mxs;

		this.nnn = new Vector3f(mns, mns, mns);
		this.pnn = new Vector3f(mxs, mns, mns);
		this.pnp = new Vector3f(mxs, mns, mxs);
		this.nnp = new Vector3f(mns, mns, mxs);
		this.npn = new Vector3f(mns, mxs, mns);
		this.ppn = new Vector3f(mxs, mxs, mns);
		this.ppp = new Vector3f(mxs, mxs, mxs);
		this.npp = new Vector3f(mns, mxs, mxs);

		this.faces = new Face[]{
			new Face(nnp, nnn, pnn, pnp, Directions.ALL[0].getUnitVec3f()), // down
			new Face(npn, npp, ppp, ppn, Directions.ALL[1].getUnitVec3f()), // up
			new Face(ppn, pnn, nnn, npn, Directions.ALL[2].getUnitVec3f()), // north
			new Face(npp, nnp, pnp, ppp, Directions.ALL[3].getUnitVec3f()), // south
			new Face(npn, nnn, nnp, npp, Directions.ALL[4].getUnitVec3f()), // west
			new Face(ppp, pnp, pnn, ppn, Directions.ALL[5].getUnitVec3f()), // east
		};

		this.edges = new Line3f[]{
			new Line3f(nnn, nnp),
			new Line3f(nnn, pnn),
			new Line3f(nnn, npn),
			new Line3f(pnn, pnp),
			new Line3f(pnn, ppn),
			new Line3f(npn, pnp),
			new Line3f(npn, npp),
			new Line3f(pnp, ppp),
			new Line3f(ppn, ppp),
			new Line3f(npp, ppp),
		};
	}

	@Override
	public void accept(@Nullable Consumer<Vector3f> function) {
		nnn.set(mns, mns, mns);
		pnn.set(mxs, mns, mns);
		pnp.set(mxs, mns, mxs);
		nnp.set(mns, mns, mxs);
		npn.set(mns, mxs, mns);
		ppn.set(mxs, mxs, mns);
		ppp.set(mxs, mxs, mxs);
		npp.set(mns, mxs, mxs);

		if (function != null) {
			function.accept(nnn);
			function.accept(pnn);
			function.accept(pnp);
			function.accept(nnp);
			function.accept(npn);
			function.accept(ppn);
			function.accept(ppp);
			function.accept(npp);
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

	public void forEachVertex(VertexCallback callback) {
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
