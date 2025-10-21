package dev.latvian.mods.klib.interpolation;

import net.neoforged.bus.api.Event;

import java.util.List;

public class InterpolationTypeRegistryEvent extends Event {
	private final List<InterpolationType<?>> types;

	public InterpolationTypeRegistryEvent(List<InterpolationType<?>> types) {
		this.types = types;
	}

	public void register(InterpolationType<?> type) {
		this.types.add(type);
	}
}
