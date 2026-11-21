package dev.latvian.mods.klib.block;

import dev.latvian.mods.klib.core.api.common.KLibBlockState;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public record KLibBlockStateProperties(
	BlockState state,
	boolean visible,
	boolean partial,
	float density,
	boolean fluid
) {
	public static final Map<BlockState, KLibBlockStateProperties> MAP = new Reference2ObjectOpenHashMap<>();

	public static KLibBlockStateProperties compute(BlockState state) {
		var b = state.getBlock();

		boolean visible = state.getRenderShape() != RenderShape.INVISIBLE || !state.getFluidState().isEmpty();
		float density;

		if (state.isAir() || b instanceof LightBlock || b instanceof BarrierBlock || b instanceof FireBlock) {
			density = 0F;
		} else if (b instanceof CarpetBlock || b instanceof ButtonBlock || b instanceof PressurePlateBlock || b instanceof VineBlock || b instanceof LadderBlock) {
			density = 0.06125F;
		} else if (b instanceof DoorBlock || b instanceof SnowLayerBlock || b instanceof FlowerPotBlock) {
			density = 0.125F;
		} else if (b instanceof SlabBlock || b instanceof CrossCollisionBlock || b instanceof FenceGateBlock || b instanceof EnchantingTableBlock) {
			density = 0.5F;
		} else if (b instanceof VegetationBlock) {
			density = 0.25F;
		} else if (b instanceof SimpleWaterloggedBlock || b instanceof HopperBlock) {
			density = 0.75F;
		} else {
			density = 1F;
		}

		boolean partial = !visible || density < 1F || b instanceof AirBlock || b instanceof HalfTransparentBlock || !state.getFluidState().isEmpty();
		boolean fluid = !state.isAir() && state.liquid();

		return new KLibBlockStateProperties(
			state,
			visible,
			partial,
			density,
			fluid
		);
	}

	public static KLibBlockStateProperties of(BlockState state) {
		if (state instanceof KLibBlockState k) {
			return k.klib$getBlockStateProperties();
		} else {
			return MAP.computeIfAbsent(state, KLibBlockStateProperties::compute);
		}
	}
}
