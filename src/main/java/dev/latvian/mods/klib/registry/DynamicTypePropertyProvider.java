package dev.latvian.mods.klib.registry;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface DynamicTypePropertyProvider {
	@Nullable
	<P> P getDynamicTypeProperty(int property, Class<P> type);
}
