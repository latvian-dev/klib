package dev.latvian.mods.klib.core;

import com.mojang.brigadier.context.CommandContext;
import dev.latvian.mods.klib.entity.EntityUtils;
import dev.latvian.mods.klib.entity.filter.EntityTypeFilter;
import dev.latvian.mods.klib.util.IntOrUUID;
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
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface KLibLevel {
	default float klib$getDelta() {
		return 1F;
	}

	@Nullable
	default Entity klib$getEntityByUUID(UUID uuid) {
		throw new NoMixinException(this);
	}

	@Nullable
	default Entity klib$getEntity(IntOrUUID id) {
		return id.getEntity((Level) this);
	}

	default Iterable<Entity> klib$allEntities() {
		return ((Level) this).getEntities((Entity) null, AABB.INFINITE, Entity::isAlive);
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

		for (var player : ((Level) this).players()) {
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
		return (List) ((Level) this).getEntities(ignoredEntity, box, EntityUtils::isDamageable);
	}

	default void klib$discardAll(Predicate<Entity> filter) {
	}

	default void klib$discardAll(EntityType<?> type) {
		klib$discardAll(new EntityTypeFilter(type));
	}

	default void klib$killAll(Predicate<Entity> filter) {
	}

	default void klib$killAll(EntityType<?> type) {
		klib$killAll(new EntityTypeFilter(type));
	}

	default Stream<LevelChunk> klib$getChunks() {
		return Stream.empty();
	}

	default boolean klib$getTickDayTime() {
		return true;
	}
}
