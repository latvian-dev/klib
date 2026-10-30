package dev.latvian.mods.klib.util;

public enum PauseType {
	NONE,
	TICK,
	GAME;

	public boolean tick() {
		return this == NONE;
	}
}
