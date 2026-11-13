package dev.latvian.mods.klib.math;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

public class HitBuilder {
	public final Vec3 from;
	public final Vec3 to;
	public final Vec3 delta;
	public final Vec3 normalizedDelta;
	public final double totalDistance;
	public Vec3 hit;
	public double distanceSq;
	public double relativeHitDistance;
	public Direction direction;
	public final Vector2d temp2d;

	public HitBuilder(Vec3 from, Vec3 to) {
		this.from = from;
		this.to = to;
		this.totalDistance = from.distanceTo(to);
		this.hit = null;
		this.relativeHitDistance = 1D;
		this.distanceSq = totalDistance * totalDistance;
		this.delta = to.subtract(from);
		this.normalizedDelta = delta.normalize();
		this.direction = Direction.getApproximateNearest(delta.x, delta.y, delta.z).getOpposite();
		this.temp2d = new Vector2d();
	}

	public void offer(BlockHitResult result) {
		if (hit == null) {
			hit = result.getLocation();
			distanceSq = from.distanceToSqr(hit);
			relativeHitDistance = Math.sqrt(distanceSq) / totalDistance;
			direction = result.getDirection();
		} else {
			var newDist = from.distanceToSqr(result.getLocation());

			if (newDist < distanceSq) {
				hit = result.getLocation();
				distanceSq = newDist;
				relativeHitDistance = Math.sqrt(distanceSq) / totalDistance;
				direction = result.getDirection();
			}
		}
	}

	public void offer(Vec3 newPos, @Nullable Direction newDirection) {
		var newDist = from.distanceToSqr(newPos);

		if (newDist < distanceSq) {
			hit = newPos;
			distanceSq = newDist;
			relativeHitDistance = Math.sqrt(distanceSq) / totalDistance;
			direction = newDirection;
		}
	}

	public void offer(Vec3 newPos) {
		offer(newPos, direction);
	}

	public void offer(double newRelativeHitDistance, @Nullable Direction newDirection) {
		if (newRelativeHitDistance < relativeHitDistance) {
			hit = from.lerp(to, newRelativeHitDistance);
			distanceSq = Mth.square(totalDistance * newRelativeHitDistance);
			relativeHitDistance = newRelativeHitDistance;
			direction = newDirection;
		}
	}

	public void offer(double newRelativeHitDistance) {
		offer(newRelativeHitDistance, direction);
	}
}
