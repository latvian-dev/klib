package dev.latvian.mods.klib.platform;

import net.minecraft.util.StringRepresentable;

public enum PlatformType implements StringRepresentable {
	VANILLA("vanilla"),
	NEOFORGE("neoforge"),
	BUKKIT("bukkit");

	private final String name;

	PlatformType(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
