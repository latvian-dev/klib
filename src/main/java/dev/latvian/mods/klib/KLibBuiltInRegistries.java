package dev.latvian.mods.klib;

import dev.latvian.mods.klib.environment.KLibEnvironmentAttributes;
import dev.latvian.mods.klib.net.SyncCustomRegistriesTask;
import dev.latvian.mods.klib.particle.KLibParticles;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.network.ConfigurationTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class KLibBuiltInRegistries {
	public static final List<Supplier<ConfigurationTask>> CONFIGURATION_TASKS = new ArrayList<>();

	static {
		CONFIGURATION_TASKS.add(SyncCustomRegistriesTask::new);
	}

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
