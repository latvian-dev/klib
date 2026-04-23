package dev.latvian.mods.klib.vertex;

public record VertexCallbackWithLineWidth(VertexCallback delegate, float lineWidth) implements DelegatingVertexCallback {
	@Override
	public VertexCallback acceptPos(float x, float y, float z) {
		return delegate.acceptPos(x, y, z).acceptLineWidth(lineWidth);
	}

	@Override
	public VertexCallback acceptLineWidth(float lineWidth) {
		return this;
	}
}
