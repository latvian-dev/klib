package dev.latvian.mods.klib.vertex;

public record VertexCallbackWithNormal(VertexCallback delegate, float nx, float ny, float nz) implements DelegatingVertexCallback {
	@Override
	public VertexCallback acceptPos(float x, float y, float z) {
		return delegate.acceptPos(x, y, z).acceptNormal(nx, ny, nz);
	}

	@Override
	public VertexCallback acceptNormal(float nx, float ny, float nz) {
		return this;
	}
}
