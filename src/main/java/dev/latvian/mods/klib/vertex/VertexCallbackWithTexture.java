package dev.latvian.mods.klib.vertex;

import dev.latvian.mods.klib.texture.UV;

public record VertexCallbackWithTexture(VertexCallback delegate, UV tex) implements DelegatingVertexCallback {
	@Override
	public VertexCallback acceptTex(float u, float v) {
		return delegate.acceptTex(tex.u(u), tex.v(v));
	}
}
