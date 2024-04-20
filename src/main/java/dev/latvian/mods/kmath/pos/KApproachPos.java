package dev.latvian.mods.kmath.pos;

import dev.latvian.mods.kmath.num.KNumber;
import org.joml.Vector3d;

public class KApproachPos implements KPos {
	public final KPos kpos;
	public final Vector3d approach = new Vector3d(0D, 0D, 0D);
	public final Vector3d temp = new Vector3d(0D, 0D, 0D);
	public Number speed = 0.2D;
	public Number minDistance = KNumber.ZERO;
	public Number avoid = KNumber.ZERO;
	public Number distExp = 0.25D;

	public KApproachPos(KPos kpos) {
		this.kpos = kpos;
	}

	@Override
	public void tick(Vector3d pos) {
		kpos.tick(approach);

		var _speed = speed.doubleValue();

		if (_speed <= 0D) {
			pos.set(approach);
			return;
		}

		var _minDistance = minDistance.doubleValue();

		var dist = approach.distance(pos.x, pos.y, pos.z);

		if (dist > _minDistance || avoid.doubleValue() != 0D) {
			temp.set(approach);
			temp.sub(pos);

			if (_minDistance > 0D) {
				temp.mul((dist - _minDistance) / dist);
				dist -= _minDistance;
			}

			double s = Math.min(_speed, dist) / Math.pow(dist, distExp.doubleValue());
			pos.add(temp.x * s, temp.y * s, temp.z * s);
		}
	}
}
