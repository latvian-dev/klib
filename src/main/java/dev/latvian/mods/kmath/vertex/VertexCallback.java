package dev.latvian.mods.kmath.vertex;

import dev.latvian.mods.kmath.KMath;
import dev.latvian.mods.kmath.color.Color;
import dev.latvian.mods.kmath.texture.PackedUV;
import dev.latvian.mods.kmath.texture.UV;
import org.joml.Matrix3fc;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

@FunctionalInterface
public interface VertexCallback {
	VertexCallback acceptPos(float x, float y, float z);

	default VertexCallback acceptTex(float u, float v) {
		return this;
	}

	default VertexCallback acceptCol(float r, float g, float b, float a) {
		return this;
	}

	default VertexCallback acceptNormal(float nx, float ny, float nz) {
		return this;
	}

	default VertexCallback acceptLight(int u, int v) {
		return this;
	}

	default VertexCallback acceptOverlay(int u, int v) {
		return this;
	}

	default VertexCallback withColor(Color color) {
		return new VertexCallbackWithColor(this, color.redf(), color.greenf(), color.bluef(), color.alphaf());
	}

	default VertexCallback withTex(UV tex) {
		return new VertexCallbackWithTexture(this, tex);
	}

	default VertexCallback withLight(PackedUV light) {
		return new VertexCallbackWithLight(this, light.u(), light.v());
	}

	default VertexCallback withOverlay(PackedUV overlay) {
		return new VertexCallbackWithOverlay(this, overlay.u(), overlay.v());
	}

	default VertexCallback withTransformedPositionsAndNormals(Matrix4fc posMatrix, Matrix3fc normalMatrix, boolean normalize) {
		boolean n = !normalize && KMath.isIdentity(normalMatrix);
		boolean p = KMath.isIdentity(posMatrix);

		if (n && p) {
			return this;
		} else if (n) {
			return new TransformedPositionsVertexCallback(this, posMatrix, new Vector3f());
		} else if (p) {
			return new TransformedNormalsVertexCallback(this, normalMatrix, normalize, new Vector3f());
		} else {
			return new TransformedVertexCallback(this, posMatrix, normalMatrix, normalize, new Vector3f());
		}
	}

	default VertexCallback withTransformedPositions(Matrix4fc matrix) {
		boolean p = KMath.isIdentity(matrix);

		if (p) {
			return this;
		} else {
			return new TransformedPositionsVertexCallback(this, matrix, new Vector3f());
		}
	}

	default VertexCallback withTransformedNormals(Matrix3fc matrix, boolean normalize) {
		boolean n = !normalize && KMath.isIdentity(matrix);

		if (n) {
			return this;
		} else {
			return new TransformedNormalsVertexCallback(this, matrix, normalize, new Vector3f());
		}
	}

	default VertexCallback onlyPos() {
		return new OnlyPosVertexCallback(this);
	}

	default VertexCallback onlyPosCol() {
		return new OnlyPosColVertexCallback(this);
	}

	default VertexCallback onlyPosTex() {
		return new OnlyPosTexVertexCallback(this);
	}

	default VertexCallback onlyPosColTex() {
		return new OnlyPosColTexVertexCallback(this);
	}

	default VertexCallback onlyPosColTexNormal() {
		return new OnlyPosColTexNormalVertexCallback(this);
	}
}
