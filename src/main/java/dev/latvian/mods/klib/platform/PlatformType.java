package dev.latvian.mods.klib.platform;

import net.minecraft.util.StringRepresentable;

public enum PlatformType implements StringRepresentable {
	NEOFORGE("neoforge"),
	BUKKIT("bukkit"),
	OTHER("other");

	private final String name;

	PlatformType(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
