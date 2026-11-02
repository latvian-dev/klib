package dev.latvian.mods.klib.util.client;

import com.mojang.blaze3d.platform.NativeImage;
import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.texture.SpriteKey;
import dev.latvian.mods.klib.texture.UV;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;

import java.util.List;

public interface ImageUtils {
	static ClientAsset.ResourceTexture assetFromPNG(Identifier png) {
		return new ClientAsset.ResourceTexture(png.withPath(png.getPath().substring(9, png.getPath().length() - 4)));
	}

	static SpriteId getSpriteId(SpriteKey key) {
		return new SpriteId(key.atlas(), key.sprite());
	}

	static UV getUV(Minecraft mc, SpriteKey spriteKey) {
		if (spriteKey.isSpecial()) {
			return UV.FULL;
		} else {
			var sprite = mc.getAtlasManager().get(getSpriteId(spriteKey));
			return new UV(sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1());
		}
	}

	static NativeImage remapImage(NativeImage src, Int2IntMap remap) {
		return src.mappedCopy(srcArgb -> {
			if (srcArgb == 0) {
				return 0;
			}

			int dstArgb = remap.getOrDefault(srcArgb & 0xFFFFFF, srcArgb);

			if (dstArgb == 0x01010101) {
				return 0x01010101;
			}

			return (dstArgb & 0xFFFFFF) | (srcArgb & 0xFF000000);
		});
	}

	static NativeImage layeredImage(List<NativeImage> layers, boolean keepMeta) {
		if (layers.isEmpty()) {
			throw new IllegalArgumentException("No layers");
		}

		int dstW = 0;
		int dstH = 0;

		for (var src : layers) {
			dstW = Math.max(dstW, src.getWidth());
			dstH = Math.max(dstH, src.getHeight());
		}

		if (dstW == 0 || dstH == 0) {
			throw new IllegalArgumentException("Invalid layer sizes");
		}

		var dst = new NativeImage(dstW, dstH, true);

		for (var src : layers) {
			int srcW = src.getWidth();
			int srcH = src.getHeight();

			for (int x = 0; x < dstW; x++) {
				for (int y = 0; y < dstH; y++) {
					int srcColorArgb = src.getPixel(x * srcW / dstW, y * srcH / dstH);

					if (srcColorArgb == 0x01010101) {
						dst.setPixel(x, y, keepMeta ? 0x01010101 : 0);
					} else {
						var srcColor = Color.of(srcColorArgb);
						var dstColor = Color.of(dst.getPixel(x, y));
						dst.setPixel(x, y, srcColor.mix(dstColor).argb());
					}
				}
			}
		}

		return dst;
	}

	static NativeImage resize(NativeImage img, int nw, int nh) {
		int ow = img.getWidth();
		int oh = img.getHeight();

		if (nw == ow && nh == oh) {
			return img;
		}

		try (var oimg = img) {
			var dst = new NativeImage(oimg.format(), nw, nh, true);

			for (int y = 0; y < nh; y++) {
				for (int x = 0; x < nw; x++) {
					int ox = x * ow / nw;
					int oy = y * oh / nh;
					dst.setPixel(x, y, oimg.getPixel(ox, oy));
				}
			}

			return dst;
		}
	}
}
