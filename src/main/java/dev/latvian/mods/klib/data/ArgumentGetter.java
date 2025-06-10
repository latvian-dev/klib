package dev.latvian.mods.klib.data;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;

@FunctionalInterface
public interface ArgumentGetter<T> {
	T get(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException;
}
