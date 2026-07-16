package dev.latvian.mods.klib.entity.filter;

import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.util.ParsedEntitySelector;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;

public record MatchEntityFilter(ParsedEntitySelector selector) implements EntityFilter {
	public static DynamicType<RegistryFriendlyByteBuf, EntityFilter> TYPE = DynamicType.create(
		"match",
		"selector",
		ParsedEntitySelector.CODEC,
		ParsedEntitySelector.STREAM_CODEC,
		MatchEntityFilter::new,
		MatchEntityFilter::selector
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, EntityFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(Entity entity) {
		var s = selector.getSelector();
		return s != null && s.test(entity);
	}

	@Override
	public boolean isStringLiteral() {
		return true;
	}

	@Override
	public @NonNull String toString() {
		return selector.getInput();
	}
}
