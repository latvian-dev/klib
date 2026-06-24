package dev.latvian.mods.klib.command;

import dev.latvian.mods.klib.KLib;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface KLibCommandArgumentTypes {
	DeferredRegister<ArgumentTypeInfo<?, ?>> REGISTRY = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, KLib.ID);

	Holder<ArgumentTypeInfo<?, ?>> ANY_ENUM = REGISTRY.register("any_enum", () -> ArgumentTypeInfos.registerByClass(AnyEnumArgument.class, new AnyEnumArgument.AnyEnumInfo()));
}
