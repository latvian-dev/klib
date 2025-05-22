package dev.latvian.mods.kmath.vertex.function;

import org.joml.SimplexNoise;
import org.joml.Vector3f;

public class AdditiveSimplexNoiseVertexFunction implements VertexFunction {
	private final float noise;
	private final float range;
	private final float noiseScale;
	private float offset;

	public AdditiveSimplexNoiseVertexFunction(float noise, float range, float noiseScale) {
		this.noise = noise;
		this.range = range;
		this.noiseScale = noiseScale;
		this.offset = 0F;
	}

	@Override
	public void accept(Vector3f v) {
		v.x += SimplexNoise.noise(noise, offset, 0F) * range;
		offset += noiseScale;
		v.y += SimplexNoise.noise(noise, offset, noiseScale) * range;
		offset += noiseScale;
		v.z += SimplexNoise.noise(noise, offset, noiseScale * 2F) * range;
		offset += noiseScale;
	}
}
