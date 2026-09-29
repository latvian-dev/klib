package dev.latvian.mods.klib.core;

import dev.latvian.mods.klib.math.Line;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public interface KLibEntity {
	default Entity klib$self() {
		return (Entity) this;
	}

	default void klib$setLevel(Level level) {
		throw new NoMixinException(this);
	}

	default boolean klib$isSaving() {
		return false;
	}

	default Line klib$ray(double distance, float delta) {
		var entity = klib$self();
		var start = entity.getEyePosition(delta);
		var end = start.add(entity.getViewVector(delta).scale(distance));
		return new Line(start, end);
	}

	default Line klib$ray(float delta) {
		return klib$ray(4.5D, delta);
	}

	default float klib$getHealth(float delta) {
		return 1F;
	}

	default float klib$getMaxHealth(float delta) {
		return 1F;
	}

	default float klib$getRelativeHealth(float delta) {
		float max = klib$getMaxHealth(delta);
		return max > 0F ? Math.clamp(klib$getHealth(delta) / max, 0F, 1F) : 0F;
	}
}
