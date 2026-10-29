package dev.latvian.mods.klib.core;

import dev.latvian.mods.klib.math.FrustumCheck;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.FrustumIntersection;

public interface KLibFrustum extends FrustumCheck {
	@Override
	default boolean isVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		int result = ((Frustum) this).cubeInFrustum(minX, minY, minZ, maxX, maxY, maxZ);
		return result == FrustumIntersection.INTERSECT || result == FrustumIntersection.INSIDE;
	}
}
