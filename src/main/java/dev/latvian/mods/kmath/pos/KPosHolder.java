package dev.latvian.mods.kmath.pos;

import com.google.gson.JsonElement;
import dev.latvian.mods.kmath.KStore;
import dev.latvian.mods.kmath.util.DeltaTicking;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3d;

public class KPosHolder implements DeltaTicking {
	public final KStore parent;
	public KPos source;
	public final Vector3d pos;
	public final Vector3d prevPos;

	public KPosHolder(KStore parent, KPos source) {
		this.parent = parent;
		this.source = source;
		this.pos = new Vector3d(0D, 0D, 0D);
		this.source.tick(pos);
		this.prevPos = new Vector3d(pos);
	}

	@Override
	public void snap() {
		prevPos.set(pos);
	}

	@Override
	public void tickValue() {
		source.tick(pos);
	}

	public double getX(float delta) {
		return MathHelper.lerp(delta, prevPos.x, pos.x);
	}

	public double getY(float delta) {
		return MathHelper.lerp(delta, prevPos.y, pos.y);
	}

	public double getZ(float delta) {
		return MathHelper.lerp(delta, prevPos.z, pos.z);
	}

	public void update(JsonElement json) {
		if (json != null) {
			source = KPos.fromJson(parent, json, pos);
		}
	}

	public boolean changed() {
		return pos.x != prevPos.x || pos.y != prevPos.y || pos.z != prevPos.z;
	}
}
