package dev.latvian.mods.klib.data;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.CommandBuildContext;

@FunctionalInterface
public interface ArgumentTypeProvider {
	ArgumentType<?> create(DataTypeCommandInfo self, CommandBuildContext ctx);

	@FunctionalInterface
	interface NS extends ArgumentTypeProvider {
		ArgumentType<?> create(CommandBuildContext ctx);

		@Override
		default ArgumentType<?> create(DataTypeCommandInfo self, CommandBuildContext ctx) {
			return create(ctx);
		}
	}

	@FunctionalInterface
	interface NSNCTX extends ArgumentTypeProvider {
		ArgumentType<?> create();

		@Override
		default ArgumentType<?> create(DataTypeCommandInfo self, CommandBuildContext ctx) {
			return create();
		}
	}
}
