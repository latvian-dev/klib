package dev.latvian.mods.klib.core;

import com.mojang.brigadier.context.CommandContext;
import dev.latvian.mods.klib.entity.EntityUtils;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface KLibLevel {
	default Level klib$self() {
		return (Level) this;
	}

	default float klib$getDelta() {
		return 1F;
	}

	default Iterable<Entity> klib$allEntities() {
		return klib$self().getEntities((Entity) null, AABB.INFINITE, Entity::isAlive);
	}

	default Iterable<LivingEntity> klib$allLivingEntities() {
		var list = new ArrayList<LivingEntity>();

		for (var entity : klib$allEntities()) {
			if (entity instanceof LivingEntity livingEntity) {
				list.add(livingEntity);
			}
		}

		return list;
	}

	default List<Entity> klib$selectEntities(EntitySelector selector) {
		var list = new ArrayList<Entity>(1);

		for (var entity : klib$allEntities()) {
			if (selector.test(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	default List<Entity> klib$selectEntities(CommandContext<?> ctx, String name) {
		return klib$selectEntities(ctx.getArgument(name, EntitySelector.class));
	}

	default List<Player> klib$selectPlayers(EntitySelector selector) {
		var list = new ArrayList<Player>(1);

		for (var player : klib$self().players()) {
			if (selector.test(player)) {
				list.add(player);
			}
		}

		return list;
	}

	default List<Player> klib$selectPlayers(CommandContext<?> ctx, String name) {
		return klib$selectPlayers(ctx.getArgument(name, EntitySelector.class));
	}

	default List<LivingEntity> klib$getDamageableEntities(@Nullable Entity ignoredEntity, AABB box) {
		return (List) klib$self().getEntities(ignoredEntity, box, EntityUtils::isDamageable);
	}

	default void klib$discardAll(Predicate<Entity> filter) {
	}

	default void klib$discardAll(EntityType<?> type) {
		klib$discardAll(entity -> entity.getType() == type);
	}

	default void klib$killAll(Predicate<Entity> filter) {
	}

	default void klib$killAll(EntityType<?> type) {
		klib$killAll(entity -> entity.getType() == type);
	}

	default Stream<LevelChunk> klib$getChunks() {
		return Stream.empty();
	}

	default boolean klib$getTickDayTime() {
		return true;
	}

	default void klib$setDayTime(long time) {
		throw new NoMixinException(this);
	}

	default boolean klib$isLocalServer() {
		return false;
	}
}
