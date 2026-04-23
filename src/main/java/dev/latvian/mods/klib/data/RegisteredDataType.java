package dev.latvian.mods.klib.data;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record RegisteredDataType<T>(
	Identifier id,
	DataType<T> type,
	@Nullable ArgumentTypeProvider<T> argumentType,
	@Nullable ArgumentGetter<T> argumentGetter
) {
	public static final Map<Identifier, RegisteredDataType<?>> BY_ID = new Object2ObjectOpenHashMap<>();
	public static final Map<DataType<?>, RegisteredDataType<?>> BY_TYPE = new Reference2ObjectOpenHashMap<>();
}
