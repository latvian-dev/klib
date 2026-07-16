package dev.latvian.mods.klib.entity;

import dev.latvian.mods.klib.math.Rotation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface EntityUtils {
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
		return entity instanceof LivingEntity && (!(entity instanceof Player) || isSurvivalLike(entity));
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

	private static Vec3 getLeashOffset(Entity entity, float delta) {
		return entity instanceof Leashable leashable ? leashable.getLeashOffset(delta) : new Vec3(0D, entity.getEyeHeight(), entity.getBbWidth() * 0.4F);
	}
}
