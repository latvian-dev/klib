package dev.latvian.mods.klib.entity;

import com.mojang.brigadier.context.CommandContext;
import dev.latvian.mods.klib.entity.filter.EntityTypeFilter;
import dev.latvian.mods.klib.math.Rotation;
import dev.latvian.mods.klib.platform.PlatformHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface EntityUtils {
	static Iterable<Entity> getAllEntities(Level level) {
		return PlatformHelper.CURRENT.getEntities(level);
	}

	static Iterable<LivingEntity> getAllLivingEntities(Level level) {
		var list = new ArrayList<LivingEntity>();

		for (var entity : getAllEntities(level)) {
			if (entity instanceof LivingEntity livingEntity) {
				list.add(livingEntity);
			}
		}

		return list;
	}

	static List<Entity> getSelectedEntities(Level level, EntitySelector selector) {
		var list = new ArrayList<Entity>(1);

		for (var entity : getAllEntities(level)) {
			if (entity.isAlive() && selector.test(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	static List<Entity> getSelectedEntities(CommandContext<CommandSourceStack> ctx, String name) {
		return getSelectedEntities(PlatformHelper.CURRENT.getLevel(ctx.getSource()), ctx.getArgument(name, EntitySelector.class));
	}

	static List<Player> getSelectedPlayers(Level level, Predicate<Entity> selector) {
		var list = new ArrayList<Player>(1);

		for (var player : level.players()) {
			if (selector.test(player)) {
				list.add(player);
			}
		}

		return list;
	}

	static List<Player> getSelectedPlayers(CommandContext<CommandSourceStack> ctx, String name) {
		return getSelectedPlayers(PlatformHelper.CURRENT.getLevel(ctx.getSource()), ctx.getArgument(name, EntitySelector.class));
	}

	static List<LivingEntity> getDamageableEntities(Level level, @Nullable Entity ignoredEntity, AABB box) {
		return (List) level.getEntities(ignoredEntity, box, EntityUtils::isDamageable);
	}

	@Nullable
	static GameType getGameMode(Entity entity) {
		if (entity instanceof Player player) {
			return player.gameMode();
		}

		return null;
	}

	static boolean isSpectatorOrCreative(Entity entity) {
		var type = getGameMode(entity);
		return type == GameType.SPECTATOR || type == GameType.CREATIVE;
	}

	static boolean isCreative(Entity entity) {
		return getGameMode(entity) == GameType.CREATIVE;
	}

	static boolean isSurvival(Entity entity) {
		return getGameMode(entity) == GameType.SURVIVAL;
	}

	static boolean isAdventure(Entity entity) {
		return getGameMode(entity) == GameType.ADVENTURE;
	}

	static boolean isSurvivalLike(Entity entity) {
		var type = getGameMode(entity);
		return type != null && type.isSurvival();
	}

	static float getHealth(Entity entity) {
		if (entity instanceof LivingEntity living) {
			return living.getHealth();
		}

		return 1F;
	}

	static float getMaxHealth(Entity entity) {
		if (entity instanceof LivingEntity living) {
			return living.getMaxHealth();
		}

		return 1F;
	}

	static float getRelativeHealth(Entity entity) {
		return Math.clamp(getHealth(entity) / getMaxHealth(entity), 0F, 1F);
	}

	static boolean hasItem(Entity entity, Predicate<ItemStack> ingredient) {
		if (entity instanceof ItemEntity itemEntity) {
			return ingredient.test(itemEntity.getItem());
		} else if (entity instanceof Player player) {
			for (var item : player.getInventory()) {
				if (!item.isEmpty() && ingredient.test(item)) {
					return true;
				}
			}

			return false;
		} else if (entity instanceof LivingEntity living) {
			for (var slot : EquipmentSlot.VALUES) {
				var stack = living.getItemBySlot(slot);

				if (!stack.isEmpty() && ingredient.test(stack)) {
					return true;
				}
			}

			return false;
		}

		return false;
	}

	static boolean isDeadOrDying(Entity entity) {
		if (entity instanceof LivingEntity living) {
			return living.isDeadOrDying();
		}

		return !entity.isAlive();
	}

	static boolean isDamageable(Entity entity) {
		return entity instanceof LivingEntity && ((!(entity instanceof Player) || isSurvivalLike(entity)));
	}

	static Rotation rotation(Entity e, float delta) {
		return Rotation.deg(e.getYRot(delta), e.getXRot(delta));
	}

	static Rotation viewRotation(Entity e, float delta) {
		return Rotation.deg(e.getViewYRot(delta), e.getViewXRot(delta));
	}

	static Vec3 getLookTarget(Entity e, float delta) {
		if (delta == 1F) {
			return e.position().add(e.getViewVector(1F));
		} else {
			return e.getPosition(delta).add(e.getViewVector(delta));
		}
	}

	static Vec3 getPosition(Entity e, PositionType type) {
		return switch (type) {
			case CENTER -> new Vec3(e.getX(), e.getY() + e.getBbHeight() / 2D, e.getZ());
			case TOP -> new Vec3(e.getX(), e.getY() + e.getBbHeight(), e.getZ());
			case EYES -> e.getEyePosition();
			case LEASH -> e.position().add(getLeashOffset(e, 1F));
			case SOUND_SOURCE -> CustomSoundSourcePosition.of(e);
			case LOOK_TARGET -> getLookTarget(e, 1F);
			default -> e.position();
		};
	}

	static Vec3 getLeashOffset(Entity entity, float delta) {
		return entity instanceof Leashable leashable ? leashable.getLeashOffset(delta) : new Vec3(0D, entity.getEyeHeight(), entity.getBbWidth() * 0.4F);
	}

	default <T extends Entity> T summon(Level level, EntityType<T> type, EntityType.EntityFactory<T> factory, Consumer<T> callback) {
		var entity = factory.create(type, level);

		if (entity != null && !level.isClientSide()) {
			callback.accept(entity);
			level.addFreshEntity(entity);
		}

		return entity;
	}

	default <T extends Entity> T summon(Level level, EntityType<T> type, Consumer<T> callback) {
		return summon(level, type, type.factory, callback);
	}

	static int discardAll(ServerLevel level, Predicate<Entity> filter) {
		int count = 0;

		for (var entity : level.getAllEntities()) {
			if (filter.test(entity)) {
				entity.discard();
				count++;
			}
		}

		return count;
	}

	static int discardAll(ServerLevel level, EntityType<?> type) {
		return discardAll(level, new EntityTypeFilter(type));
	}

	static int killAll(ServerLevel level, Predicate<Entity> filter) {
		int count = 0;

		for (var entity : level.getAllEntities()) {
			if (filter.test(entity)) {
				entity.kill(level);
				count++;
			}
		}

		return count;
	}

	static int killAll(ServerLevel level, EntityType<?> type) {
		return killAll(level, new EntityTypeFilter(type));
	}
}
