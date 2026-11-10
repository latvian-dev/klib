package dev.latvian.mods.klib.entity;

import com.mojang.brigadier.context.CommandContext;
import dev.latvian.mods.klib.entity.filter.EntityTypeFilter;
import dev.latvian.mods.klib.math.Rotation;
import dev.latvian.mods.klib.platform.PlatformHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface EntityUtils {
	static Iterable<Entity> getAllEntities(Level level) {
		return PlatformHelper.INSTANCE.getEntities(level);
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
		return getSelectedEntities(PlatformHelper.INSTANCE.getLevel(ctx.getSource()), ctx.getArgument(name, EntitySelector.class));
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
		return getSelectedPlayers(PlatformHelper.INSTANCE.getLevel(ctx.getSource()), ctx.getArgument(name, EntitySelector.class));
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

	static Rotation getRotation(Entity e, float delta) {
		return Rotation.deg(e.getYRot(delta), e.getXRot(delta));
	}

	static Rotation getViewRotation(Entity e, float delta) {
		return Rotation.deg(e.getViewYRot(delta), e.getViewXRot(delta));
	}

	static Vec3 getPosition(Entity entity, Vec3 offset, float delta) {
		if (delta == 1F && offset.x == 0D && offset.y == 0D && offset.z == 0D) {
			return entity.position();
		} else {
			double x = Mth.lerp(delta, entity.xo, entity.getX()) + offset.x;
			double y = Mth.lerp(delta, entity.yo, entity.getY()) + offset.y;
			double z = Mth.lerp(delta, entity.zo, entity.getZ()) + offset.z;
			return new Vec3(x, y, z);
		}
	}

	static Vec3 getPosition(Entity entity, double yOffset, float delta) {
		if (delta == 1F && yOffset == 0D) {
			return entity.position();
		} else {
			double x = Mth.lerp(delta, entity.xo, entity.getX());
			double y = Mth.lerp(delta, entity.yo, entity.getY()) + yOffset;
			double z = Mth.lerp(delta, entity.zo, entity.getZ());
			return new Vec3(x, y, z);
		}
	}

	static Vec3 getLookTarget(Entity e, float delta) {
		return getPosition(e, e.getViewVector(delta), delta);
	}

	static Vec3 getPosition(Entity entity, PositionType type, float delta) {
		return switch (type) {
			case CENTER -> getPosition(entity, entity.getBbHeight() / 2D, delta);
			case TOP -> getPosition(entity, delta, entity.getBbHeight());
			case EYES -> getPosition(entity, delta, entity.getEyeHeight());
			case LEASH -> getLeashPosition(entity, delta);
			case SOUND_SOURCE -> CustomSoundSourcePosition.of(entity, delta);
			case LOOK_TARGET -> getLookTarget(entity, delta);
			default -> getPosition(entity, Vec3.ZERO, delta);
		};
	}

	static Vec3 getLeashOffset(Entity entity, float delta) {
		Vec3 leashOffset;

		if (entity instanceof Leashable leashable) {
			leashOffset = leashable.getLeashOffset(delta);
		} else {
			leashOffset = new Vec3(0D, entity.getEyeHeight(), entity.getBbWidth() * 0.4D);
		}

		float entityYRot = entity.getPreciseBodyRotation(delta) * (float) (Math.PI / 180D);
		return leashOffset.yRot(-entityYRot);
	}

	static Vec3 getLeashPosition(Entity entity, float delta) {
		return getPosition(entity, getLeashOffset(entity, delta), delta);
	}

	static <T extends Entity> T summon(Level level, EntityType<T> type, EntityType.EntityFactory<T> factory, Consumer<T> callback) {
		var entity = factory.create(type, level);

		if (entity != null && !level.isClientSide()) {
			callback.accept(entity);
			level.addFreshEntity(entity);
		}

		return entity;
	}

	static <T extends Entity> T summon(Level level, EntityType<T> type, Consumer<T> callback) {
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

	@Nullable
	static Entity getEntity(ClipContext clipContext) {
		return clipContext.collisionContext instanceof EntityCollisionContext ctx ? ctx.getEntity() : null;
	}

	static void teleport(Entity entity, ServerLevel to, Vec3 pos) {
		entity.teleport(new TeleportTransition(
			to,
			pos,
			entity.getDeltaMovement(),
			entity.getYRot(),
			entity.getXRot(),
			TeleportTransition.DO_NOTHING
		));
	}

	static void teleport(Entity entity, Vec3 pos) {
		teleport(entity, (ServerLevel) entity.level(), pos);
	}

	static void teleport(Entity entity, ServerLevel to, BlockPos pos) {
		teleport(entity, to, new Vec3(pos.getX() + 0.5D, pos.getY() + 0.015625D, pos.getZ() + 0.5D));
	}

	static void teleport(Entity entity, BlockPos pos) {
		teleport(entity, new Vec3(pos.getX() + 0.5D, pos.getY() + 0.015625D, pos.getZ() + 0.5D));
	}
}
