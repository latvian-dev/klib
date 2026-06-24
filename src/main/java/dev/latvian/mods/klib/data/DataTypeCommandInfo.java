package dev.latvian.mods.klib.data;

public record DataTypeCommandInfo<T>(
	DataType<T> dataType,
	ArgumentTypeProvider<T> argumentType,
	ArgumentGetter<T> argumentGetter
) {
}
