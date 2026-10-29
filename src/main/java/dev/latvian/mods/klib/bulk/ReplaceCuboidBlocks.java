package dev.latvian.mods.klib.bulk;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public record ReplaceCuboidBlocks(BlockPos start, BlockPos end, BlockState state) implements BulkLevelModification {
	public static final DynamicType<RegistryFriendlyByteBuf, BulkLevelModification> TYPE = DynamicType.create(
		"cuboid_blocks",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockPos.CODEC.fieldOf("start").forGetter(ReplaceCuboidBlocks::start),
			BlockPos.CODEC.fieldOf("end").forGetter(ReplaceCuboidBlocks::end),
			MCCodecs.BLOCK_STATE.fieldOf("state").forGetter(ReplaceCuboidBlocks::state)
		).apply(instance, ReplaceCuboidBlocks::new)),
		CompositeStreamCodec.of(
			BlockPos.STREAM_CODEC, ReplaceCuboidBlocks::start,
			BlockPos.STREAM_CODEC, ReplaceCuboidBlocks::end,
			MCStreamCodecs.BLOCK_STATE, ReplaceCuboidBlocks::state,
			ReplaceCuboidBlocks::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, BulkLevelModification> type() {
		return TYPE;
	}

	@Override
	public void collectSections(Level level, Set<SectionPos> sections) {
		int sminX = SectionPos.blockToSectionCoord(Math.min(start.getX(), end.getX()));
		int sminY = SectionPos.blockToSectionCoord(Math.min(start.getY(), end.getY()));
		int sminZ = SectionPos.blockToSectionCoord(Math.min(start.getZ(), end.getZ()));
		int smaxX = SectionPos.blockToSectionCoord(Math.max(start.getX(), end.getX()));
		int smaxY = SectionPos.blockToSectionCoord(Math.max(start.getY(), end.getY()));
		int smaxZ = SectionPos.blockToSectionCoord(Math.max(start.getZ(), end.getZ()));

		for (int x = sminX; x <= smaxX; x++) {
			for (int y = sminY; y <= smaxY; y++) {
				for (int z = sminZ; z <= smaxZ; z++) {
					sections.add(SectionPos.of(x, y, z));
				}
			}
		}
	}

	@Override
	public void apply(BlockModificationConsumer blocks) {
		blocks.fill(start, end, state);
	}

	@Override
	public Ref<BulkLevelModification> optimize(Ref<BulkLevelModification> self) {
		if (start.equals(end)) {
			return new SingleBlockLevelModification(start, state).optimizedRef();
		}

		return self;
	}
}
