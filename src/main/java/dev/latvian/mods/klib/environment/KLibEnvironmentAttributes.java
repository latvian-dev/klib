package dev.latvian.mods.klib.environment;

import dev.latvian.mods.klib.util.Lazy;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.Identifier;
import net.minecraft.world.attribute.AttributeType;
import net.minecraft.world.attribute.EnvironmentAttribute;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public interface KLibEnvironmentAttributes {
	List<Pair<Identifier, Supplier<AttributeType<?>>>> TYPE_REGISTRY = new LinkedList<>();
	List<Pair<Identifier, Supplier<EnvironmentAttribute<?>>>> REGISTRY = new LinkedList<>();

	static <Value> Supplier<AttributeType<Value>> registerType(Identifier name, Supplier<AttributeType<Value>> value) {
		Supplier<AttributeType<Value>> supplier = Lazy.of(value);
		TYPE_REGISTRY.add(Pair.of(name, (Supplier) supplier));
		return supplier;
	}

	static <Value> Supplier<EnvironmentAttribute<Value>> register(Identifier name, Supplier<EnvironmentAttribute<Value>> value) {
		Supplier<EnvironmentAttribute<Value>> supplier = Lazy.of(value);
		REGISTRY.add(Pair.of(name, (Supplier) supplier));
		return supplier;
	}

	static void init() {
	}
}
