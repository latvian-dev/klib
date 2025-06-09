package dev.latvian.mods.klib.util;

import dev.latvian.mods.klib.math.Rotation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

@FunctionalInterface
public interface SimilarityCheck<T> {
	SimilarityCheck<?> DEFAULT = Objects::equals;
	SimilarityCheck<Float> FLOAT = SimilarityCheck::areFloatsSimilar;
	SimilarityCheck<Double> DOUBLE = SimilarityCheck::areDoublesSimilar;
	SimilarityCheck<Vec3> VEC3 = (a, b) -> areDoublesSimilar(a.x, b.x) && areDoublesSimilar(a.y, b.y) && areDoublesSimilar(a.z, b.z);
	SimilarityCheck<Rotation> ROTATION = Rotation::isSimilar;
	SimilarityCheck<ItemStack> ITEM_STACK = ItemStack::isSameItemSameComponents;

	static <T> SimilarityCheck<T> getDefault() {
		return (SimilarityCheck<T>) DEFAULT;
	}

	static boolean areFloatsSimilar(float a, float b) {
		return a == b || Math.abs(a - b) <= 0.00001F;
	}

	static boolean areDoublesSimilar(double a, double b) {
		return a == b || Math.abs(a - b) <= 0.00001D;
	}

	boolean areSimilar(T a, T b);
}
