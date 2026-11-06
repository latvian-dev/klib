package dev.latvian.mods.klib.gradient;

import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.color.PositionedColor;
import dev.latvian.mods.klib.registry.RefType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GradientImageLoader extends SimplePreparableReloadListener<List<Map.Entry<String, Gradient>>> {
	private final RefType refType;

	public GradientImageLoader(RefType refType) {
		this.refType = refType;
	}

	@Override
	protected List<Map.Entry<String, Gradient>> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
		var entries = new ArrayList<Map.Entry<String, Gradient>>();

		for (var entry : resourceManager.listResources("textures/klib/gradient", id -> !id.getPath().startsWith("_") && id.getPath().endsWith(".png")).entrySet()) {
			try (var in = entry.getValue().open()) {
				var id = entry.getKey().withPath(s -> s.substring(23, s.length() - 4));
				var image = ImageIO.read(in);
				var pixels = new ArrayList<Color>(image.getWidth());

				for (int x = 0; x < image.getWidth(); x++) {
					pixels.add(Color.of(image.getRGB(x, 0)));
				}

				var colorList = PositionedColor.fromSimpleList(pixels);

				if (!colorList.isEmpty()) {
					entries.add(Map.entry(id.getPath(), new CompoundGradient(colorList).optimize()));
				}
			} catch (Exception ex) {
				KLib.LOGGER.error("Error while reading file " + entry.getKey(), ex);
			}
		}

		return entries;
	}

	@Override
	protected void apply(List<Map.Entry<String, Gradient>> entries, ResourceManager resourceManager, ProfilerFiller profiler) {
		Gradient.REGISTRY.updateValues(entries, refType);
	}
}
