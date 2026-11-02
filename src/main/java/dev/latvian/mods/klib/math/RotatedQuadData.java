package dev.latvian.mods.klib.math;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RotatedQuadData {
	public final Vector3f tl, bl, br, tr;
	private AAFBB clipBox;

	public RotatedQuadData() {
		this.tl = new Vector3f();
		this.bl = new Vector3f();
		this.br = new Vector3f();
		this.tr = new Vector3f();
		this.clipBox = AAFBB.INFINITE;
	}

	public void update(Matrix4f bbMat, float width, float height) {
		float w2 = width / 2F;
		float h2 = height / 2F;

		tl.set(-w2, h2, 0F).mulPosition(bbMat);
		bl.set(-w2, -h2, 0F).mulPosition(bbMat);
		br.set(w2, -h2, 0F).mulPosition(bbMat);
		tr.set(w2, h2, 0F).mulPosition(bbMat);
		clipBox = AAFBB.INFINITE;
	}

	public AAFBB getClipBox() {
		if (clipBox == AAFBB.INFINITE) {
			clipBox = new AAFBB(
				Math.min(Math.min(tl.x, bl.x), Math.min(br.x, tr.x)) - 0.05F,
				Math.min(Math.min(tl.y, bl.y), Math.min(br.y, tr.y)) - 0.05F,
				Math.min(Math.min(tl.z, bl.z), Math.min(br.z, tr.z)) - 0.05F,
				Math.max(Math.max(tl.x, bl.x), Math.max(br.x, tr.x)) + 0.05F,
				Math.max(Math.max(tl.y, bl.y), Math.max(br.y, tr.y)) + 0.05F,
				Math.max(Math.max(tl.z, bl.z), Math.max(br.z, tr.z)) + 0.05F
			);
		}

		return clipBox;
	}
}
