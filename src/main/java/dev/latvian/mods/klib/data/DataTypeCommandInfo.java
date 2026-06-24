package dev.latvian.mods.klib.data;

public record DataTypeCommandInfo(
	ArgumentTypeProvider argumentType,
	ArgumentGetter<?> argumentGetter
) {
}
