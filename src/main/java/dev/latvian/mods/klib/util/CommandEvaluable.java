package dev.latvian.mods.klib.util;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface CommandEvaluable {
	@Nullable
	Component evaluate(CommandSourceStack source);
}
