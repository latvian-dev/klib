package dev.latvian.mods.kmath;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod(KMathMod.ID)
@EventBusSubscriber(modid = KMathMod.ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class KMathMod {
	public static final String ID = "kmath";
	public static final String NAME = "KMath";

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}

	@SubscribeEvent
	public static void setup(FMLLoadCompleteEvent event) {
		event.enqueueWork(KMathMod::test);
	}

	private static void test() {
		/*
		var gradient = new CompoundGradient(List.of(new PairGradient(Color.GREEN, Color.WHITE, Easing.QUINT_IN), new CompoundGradient(List.of(Color.BLUE, Color.RED), Easing.QUINT_IN)));

		try (var img = new NativeImage(NativeImage.Format.RGBA, 60, 20, false)) {
			for (int x = 0; x < img.getWidth(); x++) {
				int col = gradient.get(x / (img.getWidth() - 1F)).abgr();

				for (int y = 0; y < img.getHeight(); y++) {
					img.setPixelABGR(x, y, col);
				}
			}

			img.writeToFile(FMLPaths.CONFIGDIR.get().resolve("test.png"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		 */

		/*
		try (var reader = Files.newBufferedReader(FMLPaths.CONFIGDIR.get().resolve("test.json"))) {
			var json = new GsonBuilder().create().fromJson(reader, JsonObject.class);

			var gradients = Gradient.CODEC.listOf().parse(JsonOps.INSTANCE, json.get("gradients")).getOrThrow();

			for (var gradient : gradients) {
				System.out.println(gradient + " -> " + gradient.resolve());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		 */
	}
}
