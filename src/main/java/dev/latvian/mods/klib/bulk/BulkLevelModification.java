package dev.latvian.mods.klib.bulk;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.List;

public interface BulkLevelModification extends CustomRegistryValue<RegistryFriendlyByteBuf, BulkLevelModification> {
	UnitType<RegistryFriendlyByteBuf, BulkLevelModification> NONE = UnitType.create("none", NoneLevelModification.INSTANCE);

	CustomRegistry<RegistryFriendlyByteBuf, BulkLevelModification> REGISTRY = CustomRegistry.create("bulk_level_modification", NONE);

	Codec<Ref<BulkLevelModification>> CODEC = REGISTRY.codec();
	StreamCodec<RegistryFriendlyByteBuf, Ref<BulkLevelModification>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<BulkLevelModification>> DATA_TYPE = REGISTRY.dataType();

	static void builtInTypes(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, BulkLevelModification> registry) {
		registry.register(BulkLevelModificationBundle.TYPE);
		registry.register(SingleBlockLevelModification.TYPE);
		registry.register(ReplaceCuboidBlocks.TYPE);
		registry.register(ReplaceSphereBlocks.TYPE);
		registry.register(ReplaceSectionBlocks.TYPE);
		registry.register(ReplaceAllSectionBlocks.TYPE);
	}

	static Ref<BulkLevelModification> allOf(List<Ref<BulkLevelModification>> list) {
		return list.isEmpty() ? NONE : list.size() == 1 ? list.getFirst() : new BulkLevelModificationBundle(list).ref();
	}

	@Override
	default CustomRegistry<RegistryFriendlyByteBuf, BulkLevelModification> getRegistry() {
		return REGISTRY;
	}

	void collectSections(Level level, LongSet sections);

	void apply(BlockModificationConsumer blocks);
}
