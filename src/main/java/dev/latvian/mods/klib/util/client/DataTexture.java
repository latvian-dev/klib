package dev.latvian.mods.klib.util.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuTexture;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.Dumpable;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;

public class DataTexture extends AbstractTexture implements Dumpable {
	private final String name;
	private final int expectedWidth;
	private final List<IntArrayList> pixels;
	private NativeImage image;
	private int currentRow;
	private int columnCount;

	public DataTexture(String name, int expectedWidth, int expectedHeight) {
		this.name = name;
		this.expectedWidth = expectedWidth;
		int width = Mth.ceil((expectedWidth + 1) / 4D) * 4;
		int height = Mth.ceil((expectedHeight + 1) / 4D) * 4;
		this.texture = RenderSystem.getDevice().createTexture(name, GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_COPY_DST, GpuFormat.RGBA8_UNORM, width, height, 1, 1);
		this.textureView = RenderSystem.getDevice().createTextureView(texture);
		this.sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);
		this.pixels = new ArrayList<>(expectedHeight);
	}

	@Override
	public void close() {
		super.close();

		if (image != null) {
			image.close();
			image = null;
		}
	}

	public void beginUpdate() {
		currentRow = 0;
		columnCount = 0;
	}

	private void countPrevRow() {
		if (currentRow > 0) {
			columnCount = Math.max(columnCount, pixels.get(currentRow - 1).size());
		}
	}

	public IntArrayList nextRow() {
		countPrevRow();
		IntArrayList row;

		if (currentRow >= pixels.size()) {
			row = new IntArrayList(expectedWidth);
			pixels.add(row);
		} else {
			row = pixels.get(currentRow);
			row.clear();
		}

		currentRow++;
		return row;
	}

	public int endUpdate() {
		countPrevRow();

		if (columnCount == 0 || currentRow == 0) {
			return 0;
		}

		int width = Mth.ceil((columnCount + 1) / 4D) * 4;
		int height = Mth.ceil((currentRow + 1) / 4D) * 4;

		if (image == null || width > image.getWidth() || height > image.getHeight()) {
			if (image != null) {
				width = Math.max(width, image.getWidth());
				height = Math.max(height, image.getHeight());
				image.close();
				image = null;
			}

			image = new NativeImage(width, height, false);

			if (texture != null) {
				texture.close();
			}

			if (textureView != null) {
				textureView.close();
			}

			texture = RenderSystem.getDevice().createTexture(name, GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_COPY_DST, GpuFormat.RGBA8_UNORM, width, height, 1, 1);
			textureView = RenderSystem.getDevice().createTextureView(texture);
		}

		for (int y = 0; y < currentRow; y++) {
			var list = pixels.get(y);

			for (int x = 0; x < list.size(); x++) {
				image.setPixel(x, y, list.getInt(x));
			}
		}

		RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, image, 0, 0, 0, 0);
		return currentRow;
	}

	@Override
	public void dumpContents(Identifier id, Path path) {
		TextureUtil.writeAsPNG(path, id.toDebugFileName(), getTexture(), 0, IntUnaryOperator.identity());
	}
}
