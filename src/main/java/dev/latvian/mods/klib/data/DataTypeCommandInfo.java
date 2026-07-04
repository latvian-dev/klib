package dev.latvian.mods.klib.data;

import org.jetbrains.annotations.Nullable;

public record DataTypeCommandInfo<T>(
	DataType<?> dataType,
	ArgumentTypeProvider<T> argumentType,
	@Nullable ArgumentGetter<T> argumentGetter
) {
}
