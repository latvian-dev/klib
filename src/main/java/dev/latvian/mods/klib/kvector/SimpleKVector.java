package dev.latvian.mods.klib.kvector;

import dev.latvian.mods.klib.knumber.KNumberContext;
import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public record SimpleKVector(UnitType<RegistryFriendlyByteBuf, KVector> type, Function<KNumberContext, Vec3> factory) implements KVector {
	@Override
	public @Nullable Vec3 get(KNumberContext ctx) {
		return factory.apply(ctx);
	}

	@Override
	public @NotNull String toString() {
		return type.key();
	}
}
