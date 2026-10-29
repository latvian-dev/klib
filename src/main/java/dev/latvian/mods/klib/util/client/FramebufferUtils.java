package dev.latvian.mods.klib.util.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBuffer;

public class FramebufferUtils {
	public static NativeImage capture(RenderTarget framebuffer) {
		return capture(framebuffer, 0, false, false);
	}

	public static NativeImage capture(RenderTarget framebuffer, int mipLevel, boolean flipX, boolean flipY) {
		var colorTexture = framebuffer.getColorTexture();

		if (colorTexture == null) {
			throw new IllegalStateException("Tried to capture screenshot of an incomplete framebuffer");
		}

		var device = RenderSystem.getDevice();
		int mipWidth = colorTexture.getWidth(mipLevel);
		int mipHeight = colorTexture.getHeight(mipLevel);
		int blockSize = colorTexture.getFormat().blockSize();
		long size = (long) blockSize * mipWidth * mipHeight;
		var buffer = device.createBuffer(() -> "Screenshot buffer", GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_MAP_READ, size);

		device.createCommandEncoder().copyTextureToBuffer(colorTexture, buffer, 0L, () -> {
		}, mipLevel, 0, 0, mipWidth, mipHeight);

		var img = new NativeImage(mipWidth, mipHeight, false);

		try (var view = buffer.map(true, false)) {
			var buf = view.data();

			for (int y = 0; y < mipHeight; y++) {
				for (int x = 0; x < mipWidth; x++) {
					int col = buf.getInt((x + y * mipWidth) * blockSize);
					img.setPixelABGR(flipX ? (mipWidth - x - 1) : x, flipY ? (mipHeight - y - 1) : y, col);
				}
			}
		} finally {
			buffer.close();
		}

		return img;
	}
}
