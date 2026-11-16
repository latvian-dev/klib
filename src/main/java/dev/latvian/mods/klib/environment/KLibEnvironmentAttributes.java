package dev.latvian.mods.klib.environment;

import dev.latvian.mods.klib.util.Lazy;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.Identifier;
import net.minecraft.world.attribute.EnvironmentAttribute;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public interface KLibEnvironmentAttributes {
	List<Pair<Identifier, Supplier<EnvironmentAttribute<?>>>> REGISTRY = new LinkedList<>();

	static <T> Supplier<EnvironmentAttribute<T>> register(Identifier name, Supplier<EnvironmentAttribute.Builder<T>> builder) {
		Supplier<EnvironmentAttribute<T>> supplier = Lazy.of(() -> builder.get().build());
		REGISTRY.add(Pair.of(name, (Supplier) supplier));
		return supplier;
	}

	static void init() {
	}
}
