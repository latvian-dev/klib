package dev.latvian.mods.klib.knumber;

import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record CosKNumber(Ref<KNumber> angle) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"cos",
		"angle",
		KNumber.CODEC,
		KNumber.STREAM_CODEC,
		CosKNumber::new,
		CosKNumber::angle
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KNumber> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Double get(KNumberContext ctx) {
		var angle = this.angle.value().get(ctx);

		if (angle == null) {
			return null;
		}

		return Math.cos(Math.toRadians(angle));
	}
}
