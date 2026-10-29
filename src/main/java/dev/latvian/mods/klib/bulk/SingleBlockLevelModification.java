package dev.latvian.mods.klib.bulk;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public record SingleBlockLevelModification(BlockPos pos, BlockState state) implements BulkLevelModification {
	public static final DynamicType<RegistryFriendlyByteBuf, BulkLevelModification> TYPE = DynamicType.create(
		"block",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockPos.CODEC.fieldOf("pos").forGetter(SingleBlockLevelModification::pos),
			MCCodecs.BLOCK_STATE.fieldOf("state").forGetter(SingleBlockLevelModification::state)
		).apply(instance, SingleBlockLevelModification::new)),
		CompositeStreamCodec.of(
			BlockPos.STREAM_CODEC, SingleBlockLevelModification::pos,
			MCStreamCodecs.BLOCK_STATE, SingleBlockLevelModification::state,
			SingleBlockLevelModification::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, BulkLevelModification> type() {
		return TYPE;
	}

	@Override
	public void collectSections(Level level, Set<SectionPos> sections) {
		sections.add(SectionPos.of(pos));
	}

	@Override
	public void apply(BlockModificationConsumer blocks) {
		blocks.set(pos, state);
	}

	@Override
	public String toString() {
		return "SingleBlock[%d,%d,%d,%s]".formatted(pos.getX(), pos.getY(), pos.getZ(), BlockUtils.toString(state));
	}
}
