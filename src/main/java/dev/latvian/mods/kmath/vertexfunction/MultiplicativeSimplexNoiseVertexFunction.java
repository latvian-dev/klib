package dev.latvian.mods.kmath.vertexfunction;

import org.joml.SimplexNoise;
import org.joml.Vector3f;

public class MultiplicativeSimplexNoiseVertexFunction implements VertexFunction {
	private final float noise;
	private final float mod;
	private final float noiseScale;
	private float offset;

	public MultiplicativeSimplexNoiseVertexFunction(float noise, float mod, float noiseScale) {
		this.noise = noise;
		this.mod = mod;
		this.noiseScale = noiseScale;
		this.offset = 0F;
	}

	@Override
	public void accept(Vector3f v) {
		v.x *= 1F + SimplexNoise.noise(noise, offset, 0F) * mod;
		offset += noiseScale;
		v.y *= 1F + SimplexNoise.noise(noise, offset, noiseScale) * mod;
		offset += noiseScale;
		v.z *= 1F + SimplexNoise.noise(noise, offset, noiseScale * 2F) * mod;
		offset += noiseScale;
	}
}
