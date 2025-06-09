package dev.latvian.mods.klib.vertex;

public record OnlyPosVertexCallback(VertexCallback delegate) implements VertexCallback {
	@Override
	public VertexCallback acceptPos(float x, float y, float z) {
		return delegate.acceptPos(x, y, z);
	}
}
