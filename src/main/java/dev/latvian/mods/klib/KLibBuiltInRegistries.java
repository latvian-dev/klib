package dev.latvian.mods.klib;

import dev.latvian.mods.klib.environment.KLibEnvironmentAttributes;
import dev.latvian.mods.klib.particle.KLibParticles;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

import java.util.function.Predicate;

public class KLibBuiltInRegistries {
	public static void registerAll(Predicate<ResourceKey<?>> registryPredicate) {
		if (registryPredicate.test(Registries.PARTICLE_TYPE)) {
			for (var entry : KLibParticles.REGISTRY) {
				Registry.register(BuiltInRegistries.PARTICLE_TYPE, entry.first(), entry.second().get());
			}
		}

		if (registryPredicate.test(Registries.ENVIRONMENT_ATTRIBUTE)) {
			for (var entry : KLibEnvironmentAttributes.REGISTRY) {
				Registry.register(BuiltInRegistries.ENVIRONMENT_ATTRIBUTE, entry.first(), entry.second().get());
			}
		}
	}
}
