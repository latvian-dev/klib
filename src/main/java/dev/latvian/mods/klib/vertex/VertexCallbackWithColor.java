package dev.latvian.mods.klib.vertex;

public record VertexCallbackWithColor(VertexCallback delegate, int argb) implements DelegatingVertexCallback {
	@Override
	public VertexCallback acceptPos(float x, float y, float z) {
		return delegate.acceptPos(x, y, z).acceptCol(argb);
	}

	@Override
	public VertexCallback acceptCol(int argb) {
		return this;
	}
}
