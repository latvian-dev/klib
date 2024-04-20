package dev.latvian.mods.kmath.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Raycasting {

	/**
	 * Due to the way entities are stored (vertical entity "chunks"), collision detection fails with very tall entities.
	 * To prevent this, we offer a separate raycasting method which searches a larger vertical box, but checks the distance
	 * against any entity found to ensure it is still within our raycast.
	 * <p>
	 * Note that this method is much more performance-intensive than the alternative, so it should only be used when needed.
	 */
	public static Set<Entity> raycastVertical(World world, Vec3d from, Vec3d direction, double distance, double verticalRadius, double radius, Predicate<Entity> entityPredicate, boolean limitOne) {
		direction = direction.normalize();

		Set<Entity> found = new HashSet<>();
		for (double i = 0; i < distance; i++) {
			Box trueCollision = new Box(
				from.getX() - radius,
				from.getY() - radius,
				from.getZ() - radius,
				from.getX() + radius,
				from.getY() + radius,
				from.getZ() + radius);

			Box fakeCollision = new Box(
				from.getX() - radius,
				from.getY() - verticalRadius,
				from.getZ() - radius,
				from.getX() + radius,
				from.getY() + verticalRadius,
				from.getZ() + radius);

			List<Entity> boxed = new ArrayList<>(world.getEntitiesByClass(Entity.class, fakeCollision, entityPredicate));

			// filter by entities that collide with the actual hitbox
			boxed = boxed.stream().filter(
				entity -> entity.getBoundingBox().intersects(trueCollision)).collect(Collectors.toList());

			if (!boxed.isEmpty() && limitOne) {
				Set<Entity> set = new HashSet<>();
				set.add(boxed.get(0));
				return set;
			} else {
				found.addAll(boxed);
			}

			from = from.add(direction);
		}

		return found;
	}

	@Nullable
	public static Entity raycastOne(World world, Vec3d from, Vec3d direction, double distance, double radius, Predicate<Entity> entityPredicate) {
		direction = direction.normalize();

		Set<Entity> found = new HashSet<>();
		for (double i = 0; i < distance; i++) {
			ArrayList<Entity> boxed = new ArrayList<>(world.getEntitiesByClass(Entity.class, new Box(
				from.getX() - radius,
				from.getY() - radius,
				from.getZ() - radius,
				from.getX() + radius,
				from.getY() + radius,
				from.getZ() + radius), entityPredicate));

			if (!boxed.isEmpty()) {
				return boxed.get(0);
			}

			from = from.add(direction);
		}

		return null;
	}

	public static Set<Entity> raycast(World world, Vec3d from, Vec3d direction, double distance, double radius, Predicate<Entity> entityPredicate, boolean limitOne) {
		direction = direction.normalize();

		Set<Entity> found = new HashSet<>();
		for (double i = 0; i < distance; i++) {
			ArrayList<Entity> boxed = new ArrayList<>(world.getEntitiesByClass(Entity.class, new Box(
				from.getX() - radius,
				from.getY() - radius,
				from.getZ() - radius,
				from.getX() + radius,
				from.getY() + radius,
				from.getZ() + radius), entityPredicate));

			if (!boxed.isEmpty() && limitOne) {
				Set<Entity> set = new HashSet<>();
				set.add(boxed.get(0));
				return set;
			} else {
				found.addAll(boxed);
			}

			from = from.add(direction);
		}

		return found;
	}

	public static Vec3d distanceFromGround(Entity entity) {
		return entity.getPos().subtract(raycastDown(entity, 128, 0, false).getPos());
	}

	public static HitResult raycastDown(Entity entity, double maxDistance, float tickDelta, boolean includeFluids) {
		Vec3d cameraPosition = entity.getCameraPosVec(tickDelta);
		Vec3d rotation = new Vec3d(0, -1, 0);
		Vec3d vec3d3 = cameraPosition.add(rotation.x * maxDistance, rotation.y * maxDistance, rotation.z * maxDistance);
		return entity.getWorld().raycast(new RaycastContext(cameraPosition, vec3d3, RaycastContext.ShapeType.OUTLINE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, entity));
	}
}
