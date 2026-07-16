package dev.latvian.mods.klib.knumber;

import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public record SimpleKNumber(UnitType<RegistryFriendlyByteBuf, KNumber> type, Function<KNumberContext, Double> factory) implements KNumber {
	@Override
	public @Nullable Double get(KNumberContext ctx) {
		return factory.apply(ctx);
	}

	@Override
	public @NonNull String toString() {
		return type.key();
	}
}
