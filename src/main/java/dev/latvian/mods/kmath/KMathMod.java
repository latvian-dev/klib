package dev.latvian.mods.kmath;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;

@Mod(KMathMod.ID)
public class KMathMod {
	public static final String ID = "kmath";
	public static final String NAME = "KMath";

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}
}
