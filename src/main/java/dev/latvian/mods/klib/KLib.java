package dev.latvian.mods.klib;

import dev.latvian.mods.klib.data.DataTypeRegistry;
import dev.latvian.mods.klib.data.DataTypes;
import dev.latvian.mods.klib.data.JOMLDataTypes;

public class KLib {
	public static final String ID = "klib";
	public static final String NAME = "KLib";
	public static String VERSION = "dev";

	public static void registerDataTypes(DataTypeRegistry registry) {
		DataTypes.register(registry);
		JOMLDataTypes.register(registry);
	}
}
