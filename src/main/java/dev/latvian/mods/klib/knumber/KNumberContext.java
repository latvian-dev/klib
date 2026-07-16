package dev.latvian.mods.klib.knumber;

import dev.latvian.mods.klib.kvector.KVector;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class KNumberContext {
	public final KNumberContext parent;
	public final KNumberVariables variables;

	public Double progress;
	public Double tick;
	public Double maxTick;
	public Level level;
	public Entity entity;
	public Double gameTime;
	public Double gameDay;
	public Double clock;
	public Vec3 originPos;
	public Vec3 sourcePos;
	public Vec3 targetPos;

	@ApiStatus.Internal
	public KNumberContext() {
		this.parent = null;
		this.variables = KNumberVariables.EMPTY;

		this.progress = null;
		this.tick = null;
		this.maxTick = null;
		this.level = null;
		this.entity = null;
		this.gameTime = null;
		this.gameDay = null;
		this.clock = null;
		this.originPos = null;
		this.sourcePos = null;
		this.targetPos = null;
	}

	@ApiStatus.Internal
	public KNumberContext(KNumberVariables rootVariables) {
		this.parent = null;
		this.variables = rootVariables;

		this.progress = null;
		this.tick = null;
		this.maxTick = null;
		this.originPos = null;
		this.sourcePos = null;
		this.targetPos = null;
	}

	private KNumberContext(KNumberContext parent, @Nullable KNumberVariables variables) {
		this.parent = parent;
		this.variables = variables == null ? KNumberVariables.EMPTY : variables;

		this.progress = parent.progress;
		this.tick = parent.tick;
		this.maxTick = parent.maxTick;
		this.level = parent.level;
		this.entity = parent.entity;
		this.gameTime = parent.gameTime;
		this.gameDay = parent.gameDay;
		this.clock = parent.clock;
		this.originPos = parent.originPos;
		this.sourcePos = parent.sourcePos;
		this.targetPos = parent.targetPos;
	}

	public KNumberContext fork(@Nullable KNumberVariables variables) {
		return new KNumberContext(this, variables);
	}

	public void updateLevelData(Level level) {
		this.level = level;

		if (level != null) {
			this.gameTime = (double) level.getGameTime();
			this.gameDay = (double) (level.getGameTime() % 24000L) / 24000D;
			this.clock = (double) level.getDefaultClockTime();
		} else {
			this.gameTime = null;
			this.gameDay = null;
			this.clock = null;
		}
	}

	@Nullable
	public Ref<KNumber> getNumber(String name) {
		var ctx = this;
		Ref<KNumber> v;

		do {
			v = ctx.variables == null ? null : ctx.variables.numbers().get(name);
			ctx = ctx.parent;
		}
		while (v == null && ctx != null);

		return v;
	}

	@Nullable
	public Ref<KVector> getVector(String name) {
		var ctx = this;
		Ref<KVector> v;

		do {
			v = ctx.variables.vectors().get(name);
			ctx = ctx.parent;
		}
		while (v == null && ctx != null);

		return v;
	}
}
