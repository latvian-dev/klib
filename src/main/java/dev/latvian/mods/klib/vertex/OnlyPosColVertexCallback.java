package dev.latvian.mods.klib.vertex;

public record OnlyPosColVertexCallback(VertexCallback delegate) implements VertexCallback {
	@Override
	public VertexCallback acceptPos(float x, float y, float z) {
		return delegate.acceptPos(x, y, z);
	}

	@Override
	public VertexCallback acceptCol(int argb) {
		return delegate.acceptCol(argb);
	}
}
