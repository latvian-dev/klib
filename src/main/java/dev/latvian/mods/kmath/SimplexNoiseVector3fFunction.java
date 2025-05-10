package dev.latvian.mods.kmath;

import org.joml.SimplexNoise;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class SimplexNoiseVector3fFunction implements Consumer<Vector3f> {
	private final float noise;
	private final float range;
	private final float noiseScale;
	private float offset;

	public SimplexNoiseVector3fFunction(float noise, float range, float noiseScale) {
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
