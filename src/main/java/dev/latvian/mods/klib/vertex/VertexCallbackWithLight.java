package dev.latvian.mods.klib.vertex;

public record VertexCallbackWithLight(VertexCallback delegate, int u, int v) implements DelegatingVertexCallback {
	@Override
	public VertexCallback acceptPos(float x, float y, float z) {
		return delegate.acceptPos(x, y, z).acceptLight(u, v);
	}

	@Override
	public VertexCallback acceptLight(int u, int v) {
		return this;
	}
}
