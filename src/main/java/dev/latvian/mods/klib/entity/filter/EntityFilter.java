package dev.latvian.mods.klib.entity.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JavaOps;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.entity.EntityUtils;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.StringPrefixList;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.util.IntOrUUID;
import dev.latvian.mods.klib.util.ParsedEntitySelector;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface EntityFilter extends Predicate<Entity>, CustomRegistryValue<RegistryFriendlyByteBuf, EntityFilter> {
	StringPrefixList<EntityFilter> PREFIX_LIST = new StringPrefixList<>(EntityFilter::isStringLiteral);

	static UnitType<RegistryFriendlyByteBuf, EntityFilter> simple(String name, Predicate<Entity> predicate) {
		return UnitType.create(name, type -> new SimpleEntityFilter(type, predicate));
	}

	UnitType<RegistryFriendlyByteBuf, EntityFilter> NONE = UnitType.create("none", NoneEntityFilter.INSTANCE);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> ANY = UnitType.create("any", AnyEntityFilter.INSTANCE);

	UnitType<RegistryFriendlyByteBuf, EntityFilter> ALIVE = simple("alive", Entity::isAlive);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> DEAD = simple("dead", entity -> !entity.isAlive());
	UnitType<RegistryFriendlyByteBuf, EntityFilter> DEAD_OR_DYING = simple("dead_or_dying", EntityUtils::isDeadOrDying);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> DAMAGEABLE = simple("damageable", EntityUtils::isDamageable);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> LIVING = simple("living", entity -> entity instanceof LivingEntity);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> MOB = simple("mob", entity -> entity instanceof Mob);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> ENEMY = simple("enemy", entity -> entity instanceof Enemy);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> PLAYER = simple("player", entity -> entity instanceof Player);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> SURVIVAL_MODE = simple("survival_mode", EntityUtils::isSurvival);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> ADVENTURE_MODE = simple("adventure_mode", EntityUtils::isAdventure);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> SURVIVAL_LIKE_MODE = simple("survival_like_mode", EntityUtils::isSurvivalLike);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> CREATIVE_MODE = simple("creative_mode", EntityUtils::isCreative);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> SPECTATOR_MODE = simple("spectator_mode", Entity::isSpectator);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> SPECTATOR_OR_CREATIVE_MODE = simple("spectator_or_creative_mode", EntityUtils::isSpectatorOrCreative);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> ITEM = simple("item", entity -> entity instanceof ItemEntity);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> PROJECTILE = simple("projectile", entity -> entity instanceof Projectile);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> VISIBLE = simple("visible", entity -> !entity.isInvisible());
	UnitType<RegistryFriendlyByteBuf, EntityFilter> INVISIBLE = simple("invisible", Entity::isInvisible);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> GLOWING = simple("glowing", Entity::isCurrentlyGlowing);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> IN_WATER = simple("in_water", Entity::isInWater);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> IN_WATER_OR_RAIN = simple("in_water_or_rain", Entity::isInWaterOrRain);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> IN_LIQUID = simple("in_liquid", Entity::isInLiquid);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> UNDERWATER = simple("underwater", Entity::isUnderWater);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> ON_RAILS = simple("on_rails", Entity::isOnRails);
	UnitType<RegistryFriendlyByteBuf, EntityFilter> ON_FIRE = simple("on_fire", Entity::isOnFire);

	static EntityFilter of(boolean value) {
		return value ? AnyEntityFilter.INSTANCE : NoneEntityFilter.INSTANCE;
	}

	CustomRegistry<RegistryFriendlyByteBuf, EntityFilter> REGISTRY = CustomRegistry.<RegistryFriendlyByteBuf, EntityFilter>builder("entity_filter")
		.customCodec(direct -> KLibCodecs.or(List.of(
			Codec.BOOL.flatComapMap(EntityFilter::of, filter -> {
				if (filter == of(true)) {
					return DataResult.success(true);
				} else if (filter == of(false)) {
					return DataResult.success(false);
				} else {
					return DataResult.error(() -> "Expected either 'any' or 'none'");
				}
			}),
			IntOrUUID.CODEC.flatComapMap(ExactEntityFilter::new, filter -> {
				if (filter instanceof ExactEntityFilter(IntOrUUID id)) {
					return DataResult.success(id);
				} else {
					return DataResult.error(() -> "Filter is not an ExactEntityFilter");
				}
			}),
			PREFIX_LIST.codec(),
			direct
		)))
		.build();

	Codec<Ref<EntityFilter>> CODEC = REGISTRY.codec();
	StreamCodec<RegistryFriendlyByteBuf, Ref<EntityFilter>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<EntityFilter>> DATA_TYPE = REGISTRY.dataType();

	static void builtInTypes(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, EntityFilter> registry) {
		registry.register(NONE);
		registry.register(ANY);

		registry.register(EntityNotFilter.TYPE);
		registry.register(EntityAndFilter.TYPE);
		registry.register(EntityOrFilter.TYPE);
		registry.register(EntityXorFilter.TYPE);

		registry.register(ALIVE);
		registry.register(DEAD);
		registry.register(DEAD_OR_DYING);
		registry.register(LIVING);
		registry.register(MOB);
		registry.register(ENEMY);
		registry.register(PLAYER);
		registry.register(SURVIVAL_MODE);
		registry.register(ADVENTURE_MODE);
		registry.register(SURVIVAL_LIKE_MODE);
		registry.register(SPECTATOR_MODE);
		registry.register(CREATIVE_MODE);
		registry.register(SPECTATOR_OR_CREATIVE_MODE);
		registry.register(ITEM);
		registry.register(PROJECTILE);
		registry.register(VISIBLE);
		registry.register(INVISIBLE);
		registry.register(GLOWING);
		registry.register(IN_WATER);
		registry.register(IN_WATER_OR_RAIN);
		registry.register(IN_LIQUID);
		registry.register(UNDERWATER);
		registry.register(ON_RAILS);
		registry.register(ON_FIRE);

		registry.register(ExactEntityFilter.TYPE);
		registry.register(EntityTagFilter.TYPE);
		registry.register(EntityTypeFilter.TYPE);
		registry.register(EntityTypeTagFilter.TYPE);
		registry.register(MatchEntityFilter.TYPE);
		registry.register(HasEffectEntityFilter.TYPE);
		registry.register(ProfileEntityFilter.TYPE);
		registry.register(HasItemEntityFilter.TYPE);
		registry.register(InDimensionEntityFilter.TYPE);
		registry.register(IfEntityFilter.TYPE);

		PREFIX_LIST.add("@", input -> ParsedEntitySelector.CODEC.parse(JavaOps.INSTANCE, input).map(MatchEntityFilter::new));
	}

	@Override
	default CustomRegistry<RegistryFriendlyByteBuf, EntityFilter> getRegistry() {
		return REGISTRY;
	}

	@Nullable
	default Entity getFirst(Level level) {
		for (var entity : level.klib$allEntities()) {
			if (test(entity)) {
				return entity;
			}
		}

		return null;
	}

	default EntityFilter not() {
		return new EntityNotFilter(ref());
	}

	default EntityFilter and(EntityFilter filter) {
		if (filter == of(true)) {
			return this;
		} else if (filter == of(false)) {
			return filter;
		} else {
			return new EntityAndFilter(List.of(ref(), filter.ref()));
		}
	}

	default EntityFilter and(EntityFilter... filters) {
		var list = new ArrayList<Ref<EntityFilter>>(filters.length + 1);

		if (this != of(true)) {
			list.add(ref());
		}

		for (var filter : filters) {
			if (filter == of(false)) {
				return filter;
			} else if (filter != of(true)) {
				list.add(filter.ref());
			}
		}

		return list.size() == 1 ? list.getFirst().value() : new EntityAndFilter(List.copyOf(list));
	}

	default EntityFilter or(EntityFilter filter) {
		if (filter == of(true)) {
			return filter;
		} else if (filter == of(false)) {
			return this;
		} else {
			return new EntityOrFilter(List.of(ref(), filter.ref()));
		}
	}

	default EntityFilter or(EntityFilter... filters) {
		var list = new ArrayList<Ref<EntityFilter>>(filters.length + 1);

		if (this != of(false)) {
			list.add(ref());
		}

		for (var filter : filters) {
			if (filter == of(true)) {
				return filter;
			} else if (filter != of(false)) {
				list.add(filter.ref());
			}
		}

		return list.size() == 1 ? list.getFirst().value() : new EntityOrFilter(List.copyOf(list));
	}

	default boolean isStringLiteral() {
		return false;
	}
}
