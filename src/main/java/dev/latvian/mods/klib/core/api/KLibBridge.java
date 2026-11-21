package dev.latvian.mods.klib.core.api;

import com.mojang.serialization.Codec;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.util.EasingType;

import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class KLibBridge {
	public static List<Supplier<ConfigurationTask>> CONFIGURATION_TASKS = List.of();
	public static UnaryOperator<Codec<EasingType>> EASING_TYPE_CODEC = UnaryOperator.identity();
}
