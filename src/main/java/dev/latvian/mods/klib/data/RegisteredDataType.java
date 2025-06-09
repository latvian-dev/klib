package dev.latvian.mods.klib.data;

import com.mojang.brigadier.arguments.ArgumentType;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiFunction;

public record RegisteredDataType<T>(
	ResourceLocation id,
	DataType<T> type,
	@Nullable BiFunction<RegisteredDataType<T>, CommandBuildContext, ArgumentType<T>> argumentType
) {
	public static final Map<ResourceLocation, RegisteredDataType<?>> BY_ID = new Object2ObjectOpenHashMap<>();
	public static final Map<DataType<?>, RegisteredDataType<?>> BY_TYPE = new Reference2ObjectOpenHashMap<>();
}
