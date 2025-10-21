package dev.latvian.mods.klib.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.interpolation.Interpolation;
import dev.latvian.mods.klib.interpolation.LinearInterpolation;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

public record Color(int argb) implements Gradient {
	public static final Color[] EMPTY_ARRAY = new Color[0];

	public static final Color TRANSPARENT = new Color(0x00000000);
	public static final Color WHITE = new Color(0xFFFFFFFF);
	public static final Color BLACK = new Color(0xFF000000);
	public static final Color RED = new Color(0xFFFF0000);
	public static final Color GREEN = new Color(0xFF00FF00);
	public static final Color BLUE = new Color(0xFF0000FF);
	public static final Color YELLOW = new Color(0xFFFFFF00);
	public static final Color MAGENTA = new Color(0xFFFF00FF);
	public static final Color CYAN = new Color(0xFF00FFFF);

	public static Color of(int argb) {
		return switch (argb) {
			case 0x00000000 -> TRANSPARENT;
			case 0xFFFFFFFF -> WHITE;
			case 0xFF000000 -> BLACK;
			case 0xFFFF0000 -> RED;
			case 0xFF00FF00 -> GREEN;
			case 0xFF0000FF -> BLUE;
			case 0xFFFFFF00 -> YELLOW;
			case 0xFFFF00FF -> MAGENTA;
			case 0xFF00FFFF -> CYAN;
			default -> new Color(argb);
		};
	}

	public static Color ofRGB(int rgb) {
		return of(0xFF000000 | rgb);
	}

	public static Color of(int a, int r, int g, int b) {
		return of(((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
	}

	public static Color of(int r, int g, int b) {
		return of(255, r, g, b);
	}

	public static Color of(float a, float r, float g, float b) {
		return of((int) (a * 255F), (int) (r * 255F), (int) (g * 255F), (int) (b * 255F));
	}

	private static final Map<String, Color> NAMED_COLORS = Map.of(
		"transparent", TRANSPARENT,
		"white", WHITE,
		"black", BLACK,
		"red", RED,
		"green", GREEN,
		"blue", BLUE,
		"yellow", YELLOW,
		"magenta", MAGENTA,
		"cyan", CYAN
	);

	public static final Codec<Color> CODEC = Codec.STRING.comapFlatMap(s -> {
		var col = NAMED_COLORS.get(s);

		if (col != null) {
			return DataResult.success(col);
		} else if ((s.length() == 7 || s.length() == 9) && s.charAt(0) == '#') {
			return DataResult.success(of((s.length() == 7 ? 0xFF000000 : 0) | Integer.parseUnsignedInt(s.substring(1), 16)));
		} else {
			return DataResult.error(() -> "Invalid color format, expected #RRGGBB or #AARRGGBB: " + s);
		}
	}, Color::toString);

	public static Codec<Color> codecWithAlpha(int alpha) {
		return CODEC.xmap(color -> color.withAlpha(alpha), Color::solid);
	}

	public static Codec<Color> codecWithAlpha(float alpha) {
		return CODEC.xmap(color -> color.withAlpha(alpha), Color::solid);
	}

	public static final Codec<Color> CODEC_RGB = codecWithAlpha(255);

	public static final StreamCodec<ByteBuf, Color> STREAM_CODEC = ByteBufCodecs.INT.map(Color::of, Color::argb);

	public static final DataType<Color> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, Color.class);

	public static Color hsb(float hue, float saturation, float brightness, int alpha) {
		if (saturation <= 0F) {
			int c = (int) (brightness * 255F + 0.5F);
			return of(alpha, c, c, c);
		}

		int r = 0;
		int g = 0;
		int b = 0;
		float h = (hue - (float) Math.floor(hue)) * 6F;
		float f = h - (float) Math.floor(h);
		float p = brightness * (1F - saturation);
		float q = brightness * (1F - saturation * f);
		float t = brightness * (1F - saturation * (1F - f));

		switch ((int) h) {
			case 0:
				r = (int) (brightness * 255F + 0.5F);
				g = (int) (t * 255F + 0.5F);
				b = (int) (p * 255F + 0.5F);
				break;
			case 1:
				r = (int) (q * 255F + 0.5F);
				g = (int) (brightness * 255F + 0.5F);
				b = (int) (p * 255F + 0.5F);
				break;
			case 2:
				r = (int) (p * 255F + 0.5F);
				g = (int) (brightness * 255F + 0.5F);
				b = (int) (t * 255F + 0.5F);
				break;
			case 3:
				r = (int) (p * 255F + 0.5F);
				g = (int) (q * 255F + 0.5F);
				b = (int) (brightness * 255F + 0.5F);
				break;
			case 4:
				r = (int) (t * 255F + 0.5F);
				g = (int) (p * 255F + 0.5F);
				b = (int) (brightness * 255F + 0.5F);
				break;
			case 5:
				r = (int) (brightness * 255F + 0.5F);
				g = (int) (p * 255F + 0.5F);
				b = (int) (q * 255F + 0.5F);
		}

		return of(alpha, r, g, b);
	}

	public int rgb() {
		return argb & 0xFFFFFF;
	}

	public int alpha() {
		return (argb >> 24) & 0xFF;
	}

	public int red() {
		return (argb >> 16) & 0xFF;
	}

	public int green() {
		return (argb >> 8) & 0xFF;
	}

	public int blue() {
		return argb & 0xFF;
	}

	public float alphaf() {
		return alpha() / 255F;
	}

	public float redf() {
		return red() / 255F;
	}

	public float greenf() {
		return green() / 255F;
	}

	public float bluef() {
		return blue() / 255F;
	}

	public Color withAlpha(int alpha) {
		return of(alpha, red(), green(), blue());
	}

	public Color withAlpha(float alpha) {
		return of((int) (alpha * 255F), red(), green(), blue());
	}

	public Color fadeOut(float time, float maxTime, float fadeOut) {
		if (maxTime < fadeOut) {
			return this;
		} else if (time >= maxTime) {
			return transparent();
		} else if (time >= maxTime - fadeOut) {
			return withAlpha(Mth.lerp((maxTime - time) / fadeOut, 0F, alphaf()));
		} else {
			return this;
		}
	}

	@Override
	public int hashCode() {
		return argb;
	}

	public String toRGBString() {
		return "#%06X".formatted(rgb());
	}

	public String toARGBString() {
		return "#%08X".formatted(argb);
	}

	@Override
	public String toString() {
		return alpha() == 255 ? toRGBString() : toARGBString();
	}

	public Color lerp(float delta, Color other, int alpha) {
		if (delta <= 0F && alpha == alpha()) {
			return this;
		} else if (delta >= 1F && alpha == other.alpha()) {
			return other;
		} else {
			return of(
				alpha,
				Mth.lerpInt(delta, red(), other.red()),
				Mth.lerpInt(delta, green(), other.green()),
				Mth.lerpInt(delta, blue(), other.blue())
			);
		}
	}

	public Color lerp(float delta, Color other) {
		if (delta <= 0F || this == other) {
			return this;
		} else if (delta >= 1F) {
			return other;
		} else {
			return of(
				Mth.lerpInt(delta, alpha(), other.alpha()),
				Mth.lerpInt(delta, red(), other.red()),
				Mth.lerpInt(delta, green(), other.green()),
				Mth.lerpInt(delta, blue(), other.blue())
			);
		}
	}

	@Override
	public Color get(float delta) {
		return this;
	}

	public boolean isTransparent() {
		return alpha() == 0;
	}

	public Color solid() {
		return withAlpha(255);
	}

	public Color transparent() {
		return withAlpha(0);
	}

	public OptionalInt toOptionalARGB() {
		return isTransparent() ? OptionalInt.empty() : OptionalInt.of(argb);
	}

	public OptionalInt toOptionalRGB() {
		return isTransparent() ? OptionalInt.empty() : OptionalInt.of(rgb());
	}

	public int abgr() {
		return argb & -16711936 | (argb & 16711680) >> 16 | (argb & 255) << 16;
	}

	public float getHue() {
		int r = red();
		int g = green();
		int b = blue();

		int cmax = Math.max(r, g);

		if (b > cmax) {
			cmax = b;
		}

		int cmin = Math.min(r, g);

		if (b < cmin) {
			cmin = b;
		}

		float saturation;

		if (cmax != 0) {
			saturation = (float) (cmax - cmin) / (float) cmax;
		} else {
			saturation = 0F;
		}

		float hue;

		if (saturation == 0F) {
			hue = 0F;
		} else {
			float redc = (float) (cmax - r) / (float) (cmax - cmin);
			float greenc = (float) (cmax - g) / (float) (cmax - cmin);
			float bluec = (float) (cmax - b) / (float) (cmax - cmin);

			if (r == cmax) {
				hue = bluec - greenc;
			} else if (g == cmax) {
				hue = 2F + redc - bluec;
			} else {
				hue = 4F + greenc - redc;
			}

			hue /= 6F;

			if (hue < 0F) {
				++hue;
			}
		}

		return hue;
	}

	public float[] toHSB(float[] hsb) {
		if (hsb == null || hsb.length == 0) {
			hsb = new float[3];
		}

		int r = red();
		int g = green();
		int b = blue();

		int cmax = Math.max(r, g);

		if (b > cmax) {
			cmax = b;
		}

		int cmin = Math.min(r, g);

		if (b < cmin) {
			cmin = b;
		}

		float brightness = (float) cmax / 255F;
		float saturation;

		if (cmax != 0) {
			saturation = (float) (cmax - cmin) / (float) cmax;
		} else {
			saturation = 0F;
		}

		float hue;

		if (saturation == 0F) {
			hue = 0F;
		} else {
			float redc = (float) (cmax - r) / (float) (cmax - cmin);
			float greenc = (float) (cmax - g) / (float) (cmax - cmin);
			float bluec = (float) (cmax - b) / (float) (cmax - cmin);

			if (r == cmax) {
				hue = bluec - greenc;
			} else if (g == cmax) {
				hue = 2F + redc - bluec;
			} else {
				hue = 4F + greenc - redc;
			}

			hue /= 6F;

			if (hue < 0F) {
				++hue;
			}
		}

		hsb[0] = hue;

		if (hsb.length >= 2) {
			hsb[1] = saturation;
		}

		if (hsb.length >= 3) {
			hsb[2] = brightness;
		}

		return hsb;
	}

	@Override
	public Color optimize() {
		return this;
	}

	@Override
	public List<PositionedColor> getPositionedColors() {
		return List.of(new PositionedColor(0F, this));
	}

	public Gradient gradient(Color other) {
		return gradient(other, LinearInterpolation.INSTANCE);
	}

	public Gradient gradient(Color other, Interpolation interpolation) {
		return interpolation.isLinear() ? new LinearPairGradient(this, other) : new CompoundGradient(List.of(new PositionedColor(0F, this, interpolation), new PositionedColor(1F, other)));
	}
}
