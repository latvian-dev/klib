package dev.latvian.mods.klib.bulk;

import dev.latvian.mods.klib.registry.UnitType;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.Level;

public enum NoneLevelModification implements BulkLevelModification {
	INSTANCE;

	@Override
	public UnitType<RegistryFriendlyByteBuf, BulkLevelModification> type() {
		return NONE;
	}

	@Override
	public void collectSections(Level level, LongSet sections) {
	}

	@Override
	public void apply(BlockModificationConsumer blocks) {
	}
}
