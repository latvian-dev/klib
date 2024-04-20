package dev.latvian.mods.kmath.pos;

import dev.latvian.mods.kmath.KStore;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class KEntityUUIDPos extends KEntityPos {
	public final UUID uuid;

	public KEntityUUIDPos(KStore parent, UUID uuid) {
		super(parent, 0);
		this.uuid = uuid;
	}

	@Override
	@Nullable
	public Entity getEntity(World world) {
		return world.getEntityLookup().get(uuid);
	}
}
