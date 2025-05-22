package dev.latvian.mods.kmath.vertex;

public record OnlyPosTexVertexCallback(VertexCallback delegate) implements VertexCallback {
	@Override
	public VertexCallback acceptPos(float x, float y, float z) {
		return delegate.acceptPos(x, y, z);
	}

	@Override
	public VertexCallback acceptTex(float u, float v) {
		return delegate.acceptTex(u, v);
	}
}
