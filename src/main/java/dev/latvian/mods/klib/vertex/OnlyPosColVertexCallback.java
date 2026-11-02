package dev.latvian.mods.klib.vertex;

import dev.latvian.mods.klib.color.Color;

public record OnlyPosColVertexCallback(VertexCallback delegate) implements VertexCallback {
	@Override
	public VertexCallback acceptPos(float x, float y, float z) {
		return delegate.acceptPos(x, y, z);
	}

	@Override
	public VertexCallback acceptCol(float r, float g, float b, float a) {
		return delegate.acceptCol(r, g, b, a);
	}

	@Override
	public VertexCallback acceptCol(Color color) {
		return delegate.acceptCol(color);
	}

	@Override
	public VertexCallback acceptCol(int argb) {
		return delegate.acceptCol(argb);
	}
}
