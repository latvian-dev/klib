package dev.latvian.mods.klib.entity.filter;

import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record HasEffectEntityFilter(Holder<MobEffect> effect) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"has_effect",
		"effect",
		MobEffect.CODEC,
		MobEffect.STREAM_CODEC,
		HasEffectEntityFilter::new,
		HasEffectEntityFilter::effect
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		return entity instanceof LivingEntity living && living.hasEffect(effect);
	}
}
