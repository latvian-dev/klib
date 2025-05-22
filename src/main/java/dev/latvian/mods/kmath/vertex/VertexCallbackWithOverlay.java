package dev.latvian.mods.kmath.vertex;

public record VertexCallbackWithOverlay(VertexCallback delegate, int u, int v) implements DelegatingVertexCallback {
	@Override
	public VertexCallback acceptPos(float x, float y, float z) {
		return delegate.acceptPos(x, y, z).acceptOverlay(u, v);
	}

	@Override
	public VertexCallback acceptOverlay(int u, int v) {
		return this;
	}
}
