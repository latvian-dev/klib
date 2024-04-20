package dev.latvian.mods.kmath.pos;

import dev.latvian.mods.kmath.KStore;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class KEntitySelectorPos extends KEntityPos {
	public final EntitySelector entitySelector;

	public KEntitySelectorPos(KStore parent, EntitySelector entitySelector) {
		super(parent, 0);
		this.entitySelector = entitySelector;
	}

	@Override
	@Nullable
	public Entity getEntity(World world) {
		for (var e : world.getEntityLookup().iterate()) {
			if (entitySelector.basePredicate.test(e)) {
				return e;
			}
		}

		return null;
	}
}
