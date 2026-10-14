package dev.latvian.mods.klib.knumber;

import dev.latvian.mods.klib.entity.number.EntityNumber;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record EntityKNumber(Ref<EntityNumber> number) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"entity_number",
		"number",
		EntityNumber.CODEC,
		EntityNumber.STREAM_CODEC,
		EntityKNumber::new,
		EntityKNumber::number
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KNumber> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Double get(KNumberContext ctx) {
		return ctx.entity == null ? null : number.value().applyAsDouble(ctx.entity);
	}
}
