package dev.latvian.mods.klib;

import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.data.DataTypeCommandInfoRegistry;
import dev.latvian.mods.klib.gradient.Gradient;
import dev.latvian.mods.klib.interpolation.Interpolation;
import dev.latvian.mods.klib.platform.PlatformHelper;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryCollector;
import dev.latvian.mods.klib.shape.Shape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KLib {
	public static final String ID = "klib";
	public static final String NAME = "KLib";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	public static String VERSION = "dev";

	public static boolean writeSafeItemStacks = false;

	public static void setup() {
		var platform = PlatformHelper.CURRENT;
		CustomRegistry.registerAll(platform::collectCustomRegistries);
		DataTypeCommandInfoRegistry.registerAll(platform::collectDataTypeCommandInfos);
	}

	public static void builtInRegistries(CustomRegistryCollector registry) {
		var platform = PlatformHelper.CURRENT;
		registry.register(DataType.REGISTRY, platform::collectDataTypes);
		registry.register(Interpolation.REGISTRY, platform::collectInterpolationTypes);
		registry.register(Shape.REGISTRY, platform::collectShapeTypes);
		registry.register(Gradient.REGISTRY, platform::collectGradientTypes);
	}
}
