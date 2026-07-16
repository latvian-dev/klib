package dev.latvian.mods.klib.block.filter;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public record BlockXorFilter(Ref<BlockFilter> a, Ref<BlockFilter> b) implements BlockFilter {
	public static DynamicType<RegistryFriendlyByteBuf, BlockFilter> TYPE = DynamicType.create(
		"xor",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockFilter.CODEC.fieldOf("a").forGetter(BlockXorFilter::a),
			BlockFilter.CODEC.fieldOf("b").forGetter(BlockXorFilter::b)
		).apply(instance, BlockXorFilter::new)),
		CompositeStreamCodec.of(
			BlockFilter.STREAM_CODEC, BlockXorFilter::a,
			BlockFilter.STREAM_CODEC, BlockXorFilter::b,
			BlockXorFilter::new
		)
	);

	@Override
	public CustomRegistryType<RegistryFriendlyByteBuf, BlockFilter> type() {
		return TYPE;
	}

	@Override
	public boolean test(BlockInWorld block) {
		return a.value().test(block) ^ b.value().test(block);
	}

	@Override
	public boolean test(LevelReader level, BlockPos pos, BlockState state) {
		return a.value().test(level, pos, state) ^ b.value().test(level, pos, state);
	}
}
