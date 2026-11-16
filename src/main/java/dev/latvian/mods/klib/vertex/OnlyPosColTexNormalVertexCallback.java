package dev.latvian.mods.klib.vertex;

public record OnlyPosColTexNormalVertexCallback(VertexCallback delegate) implements VertexCallback {
	@Override
	public VertexCallback acceptPos(float x, float y, float z) {
		return delegate.acceptPos(x, y, z);
	}

	@Override
	public VertexCallback acceptCol(int argb) {
		return delegate.acceptCol(argb);
	}

	@Override
	public VertexCallback acceptTex(float u, float v) {
		return delegate.acceptTex(u, v);
	}

	@Override
	public VertexCallback acceptNormal(float nx, float ny, float nz) {
		return delegate.acceptNormal(nx, ny, nz);
	}
}
