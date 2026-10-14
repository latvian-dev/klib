package dev.latvian.mods.klib.command;

import com.mojang.brigadier.arguments.ArgumentType;
import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.util.Cast;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface KLibCommandArgumentTypes {
	DeferredRegister<ArgumentTypeInfo<?, ?>> REGISTRY = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, KLib.ID);

	static <A extends ArgumentType<?>, I extends ArgumentTypeInfo<? extends A, ?>> Holder<ArgumentTypeInfo<?, ?>> register(String name, Class<A> argumentClass, Supplier<I> argumentTypeInfo) {
		return REGISTRY.register(name, () -> ArgumentTypeInfos.registerByClass(argumentClass, Cast.to(argumentTypeInfo.get())));
	}

	Holder<ArgumentTypeInfo<?, ?>> ENUM_DATA_TYPE = register("enum_data_type", EnumDataTypeArgument.class, EnumDataTypeArgument.Info::new);
	Holder<ArgumentTypeInfo<?, ?>> PARSED_DATA_TYPE = register("parsed_data_type", ParsedDataTypeArgument.class, ParsedDataTypeArgument.Info::new);
	Holder<ArgumentTypeInfo<?, ?>> CUSTOM_REGISTRY = register("custom_registry", CustomRegistryArgument.class, CustomRegistryArgument.Info::new);
}
