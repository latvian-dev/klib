package dev.latvian.mods.klib.shape;

import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.joml.Vector3f;

public class SpherePoints {
	public static final SpherePoints L = new SpherePoints(9, 7);
	public static final SpherePoints M = new SpherePoints(24, 9);
	public static final SpherePoints H = new SpherePoints(36, 16);
	public static final SpherePoints X = new SpherePoints(108, 27);
	public static final Int2ObjectMap<SpherePoints> CACHE = new Int2ObjectOpenHashMap<>();

	public static SpherePoints get(int hd, int vd) {
		if (hd <= 9 && vd <= 7) { // 1.29
			return L;
		} else if (hd == 24 && vd == 9) { // 2.67
			return M;
		} else if (hd == 36 && vd == 16) { // 2.25
			return H;
		} else if (hd == 108 && vd == 27) { // 4.00
			return X;
		} else {
			return CACHE.computeIfAbsent((Math.clamp(hd, 9, 511) << 9) | Math.clamp(vd, 7, 511), key -> new SpherePoints((key >> 9) & 511, key & 511));
		}
	}

	public record Col(int index, float u, float x, float z) {
	}

	public record Row(int index, float v, float y, float m) {
	}

	public final int hdetail;
	public final int vdetail;
	public final Col[] cols;
	public final Row[] rows;
	public final Vector3f[][] normals;

	private SpherePoints(int hd, int vd) {
		this.hdetail = hd;
		this.vdetail = vd;
		this.cols = new Col[hd + 1];
		this.rows = new Row[vd + 1];
		this.normals = new Vector3f[hd][vd];
		calculate();
	}

	public void calculate() {
		for (int i = 0; i < cols.length; i++) {
			double d = i / (cols.length - 1D);
			this.cols[i] = new Col(i, (float) (1D - d), (float) (Math.cos(d * Math.PI * 2D) * 0.5D), (float) (Math.sin(d * Math.PI * 2D) * 0.5D));
		}

		for (int i = 1; i < rows.length - 1; i++) {
			double d = i / (rows.length - 1D);
			this.rows[i] = new Row(i, (float) d, (float) (Math.cos(d * Math.PI) * 0.5D), (float) (((Math.sin(d * Math.PI) + 1D) / 2D - 0.5D) * 2D));
		}

		rows[0] = new Row(0, 0F, 0.5F, 0F);
		rows[rows.length - 1] = new Row(rows.length - 1, 1F, -0.5F, 0F);

		for (int r = 0; r < rows.length - 1; r++) {
			for (int c = 0; c < cols.length - 1; c++) {
				var cr = rows[r];
				var nr = rows[r + 1];
				var cc = cols[c];
				var nc = cols[c + 1];

				// var n = new Vector3f(cr.x * nc.m, cr.y, cr.z * nc.m).cross(new Vector3f(nr.x * cc.m, nr.y, nr.z * cc.m)).normalize();
				normals[c][r] = new Vector3f(0F, 1F, 0F);
			}
		}

		/* FIXME
		for (int r = 0; r < rows.length - 1; r++) {
			for (int c = 0; c < cols.length - 1; c++) {
				var cr = rows[r];
				var nr = rows[r + 1];
				var cc = cols[c];
				var nc = cols[c + 1];

				var va = new Vector3f(cc.x * nr.m, nr.y, cc.z * nr.m);
				var vb = new Vector3f(cc.x * cr.m, cr.y, cc.z * cr.m);
				var vc = new Vector3f(nc.x * cr.m, cr.y, nc.z * cr.m);
				var vd = new Vector3f(nc.x * nr.m, nr.y, nc.z * nr.m);

				// normals[c][r] = vb.sub(va).mul(vc.sub(vb)).normalize();
			}
		}
		 */
	}

	public void buildQuads(float s, QuadVertexCallback callback) {
		for (int r = 0; r < rows.length - 1; r++) {
			for (int c = 0; c < cols.length - 1; c++) {
				var cr = rows[r];
				var nr = rows[r + 1];
				var cc = cols[c];
				var nc = cols[c + 1];
				var nv = normals[c][r];

				var u0l = cc.u();
				var v0l = cr.v();
				var u1l = nc.u();
				var v1l = nr.v();

				var ny = nr.y() * s;
				var cy = cr.y() * s;

				callback.quadVertex(cc.x() * nr.m() * s, ny, cc.z() * nr.m() * s, u0l, v1l, nv.x, nv.y, nv.z);
				callback.quadVertex(cc.x() * cr.m() * s, cy, cc.z() * cr.m() * s, u0l, v0l, nv.x, nv.y, nv.z);
				callback.quadVertex(nc.x() * cr.m() * s, cy, nc.z() * cr.m() * s, u1l, v0l, nv.x, nv.y, nv.z);
				callback.quadVertex(nc.x() * nr.m() * s, ny, nc.z() * nr.m() * s, u1l, v1l, nv.x, nv.y, nv.z);
			}
		}
	}

	public void buildLines(float s, LineVertexCallback callback) {
		for (int r = 0; r < rows.length - 1; r++) {
			for (int c = 0; c < cols.length - 1; c++) {
				var cr = rows[r];
				var nr = rows[r + 1];
				var cc = cols[c];
				var nc = cols[c + 1];

				var cx = cc.x() * cr.m() * s;
				var cy = cr.y() * s;
				var cz = cc.z() * cr.m() * s;

				callback.line(cc.x() * nr.m() * s, nr.y() * s, cc.z() * nr.m() * s, cx, cy, cz);
				callback.line(cx, cy, cz, nc.x() * cr.m() * s, cy, nc.z() * cr.m() * s);
			}
		}
	}
}