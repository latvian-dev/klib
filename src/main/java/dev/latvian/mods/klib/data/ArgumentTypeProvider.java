package dev.latvian.mods.klib.data;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.CommandBuildContext;

@FunctionalInterface
public interface ArgumentTypeProvider<T> {
	ArgumentType<?> create(RegisteredDataType<T> self, CommandBuildContext ctx);

	@FunctionalInterface
	interface NS<T> extends ArgumentTypeProvider<T> {
		ArgumentType<?> create(CommandBuildContext ctx);

		@Override
		default ArgumentType<?> create(RegisteredDataType<T> self, CommandBuildContext ctx) {
			return create(ctx);
		}
	}

	@FunctionalInterface
	interface NSNCTX<T> extends ArgumentTypeProvider<T> {
		ArgumentType<?> create();

		@Override
		default ArgumentType<?> create(RegisteredDataType<T> self, CommandBuildContext ctx) {
			return create();
		}
	}
}
