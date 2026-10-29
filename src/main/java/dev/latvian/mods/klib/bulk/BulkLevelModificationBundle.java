package dev.latvian.mods.klib.bulk;

import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public record BulkLevelModificationBundle(List<Ref<BulkLevelModification>> list) implements BulkLevelModification, BlockModificationConsumer {
	public static final DynamicType<RegistryFriendlyByteBuf, BulkLevelModification> TYPE = DynamicType.create(
		"bundle",
		"list",
		BulkLevelModification.CODEC.listOf(),
		KLibStreamCodecs.listOf(BulkLevelModification.STREAM_CODEC),
		BulkLevelModificationBundle::new,
		BulkLevelModificationBundle::list
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, BulkLevelModification> type() {
		return TYPE;
	}

	@Override
	public void add(BulkLevelModification modification) {
		list.add(modification.ref());
	}

	@Override
	public void set(BlockPos pos, BlockState state) {
		list.add(new SingleBlockLevelModification(pos, state).ref());
	}

	@Override
	public void fill(BlockPos start, BlockPos end, BlockState state) {
		list.add(new ReplaceCuboidBlocks(start, end, state).ref());
	}

	@Override
	public void fillSection(SectionPos pos, BlockState state) {
		list.add(new ReplaceAllSectionBlocks(pos, state).ref());
	}

	@Override
	public void applyPalettes(SectionPos pos, List<BlockPalette> palettes) {
		list.add(new ReplaceSectionBlocks(pos, palettes).ref());
	}

	@Override
	public void collectSections(Level level, LongSet sections) {
		for (var modification : list) {
			modification.value().collectSections(level, sections);
		}
	}

	@Override
	public void apply(BlockModificationConsumer blocks) {
		for (var modification : list) {
			modification.value().apply(blocks);
		}
	}

	@Override
	public Ref<BulkLevelModification> optimize(Ref<BulkLevelModification> self) {
		var tempList = new ArrayList<Ref<BulkLevelModification>>(list.size());

		for (var m : list) {
			var o = m.optimizedRef();

			if (o.value() instanceof BulkLevelModificationBundle b) {
				tempList.addAll(b.list);
			} else {
				tempList.add(o);
			}
		}

		return BulkLevelModification.allOf(tempList);
	}
}
