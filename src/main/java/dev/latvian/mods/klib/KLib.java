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

	public static void setup() {
		var helper = PlatformHelper.CURRENT;
		CustomRegistry.registerAll(helper::collectCustomRegistries);
		DataType.REGISTRY.registerTypes(helper::collectDataTypes);
		DataTypeCommandInfoRegistry.registerAll(helper::collectDataTypeCommandInfos);
		Interpolation.REGISTRY.registerTypes(helper::collectInterpolationTypes);
		Shape.REGISTRY.registerTypes(helper::collectShapeTypes);
		Gradient.REGISTRY.registerTypes(helper::collectGradientTypes);
	}

	public static void builtInRegistries(CustomRegistryCollector registry) {
		registry.register(DataType.REGISTRY);
		registry.register(Interpolation.REGISTRY);
		registry.register(Shape.REGISTRY);
		registry.register(Gradient.REGISTRY);
	}
}
