package dev.latvian.mods.klib.vertex;

public record OnlyPosColVertexCallback(VertexCallback delegate) implements VertexCallback {
	@Override
	public VertexCallback acceptPos(float x, float y, float z) {
		return delegate.acceptPos(x, y, z);
	}

	@Override
	public VertexCallback acceptCol(float r, float g, float b, float a) {
		return delegate.acceptCol(r, g, b, a);
	}
}
