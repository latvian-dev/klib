package dev.latvian.mods.kmath.color;

public record PixelGradient(Color[] pixels) implements Gradient {
	@Override
	public Color get(float delta) {
		if (delta <= 0F) {
			return pixels[0];
		} else if (delta >= 1F) {
			return pixels[pixels.length - 1];
		}

		var indexf = delta * (pixels.length - 1F);
		var indexi = (int) indexf;
		return pixels[indexi];
	}
}
