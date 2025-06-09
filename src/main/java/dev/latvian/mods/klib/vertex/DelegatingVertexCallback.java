package dev.latvian.mods.klib.vertex;

public interface DelegatingVertexCallback extends VertexCallback {
	VertexCallback delegate();

	@Override
	default VertexCallback acceptPos(float x, float y, float z) {
		return delegate().acceptPos(x, y, z);
	}

	@Override
	default VertexCallback acceptTex(float u, float v) {
		return delegate().acceptTex(u, v);
	}

	@Override
	default VertexCallback acceptCol(float r, float g, float b, float a) {
		return delegate().acceptCol(r, g, b, a);
	}

	@Override
	default VertexCallback acceptNormal(float nx, float ny, float nz) {
		return delegate().acceptNormal(nx, ny, nz);
	}

	@Override
	default VertexCallback acceptLight(int u, int v) {
		return delegate().acceptLight(u, v);
	}

	@Override
	default VertexCallback acceptOverlay(int u, int v) {
		return delegate().acceptOverlay(u, v);
	}
}
