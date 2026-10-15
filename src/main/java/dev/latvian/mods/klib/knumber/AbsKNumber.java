package dev.latvian.mods.klib.knumber;

import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record AbsKNumber(Ref<KNumber> value) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"abs",
		"value",
		KNumber.CODEC,
		KNumber.STREAM_CODEC,
		AbsKNumber::new,
		AbsKNumber::value
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KNumber> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Double get(KNumberContext ctx) {
		var value = this.value.value().get(ctx);

		if (value == null) {
			return null;
		}

		return Math.abs(value);
	}
}
