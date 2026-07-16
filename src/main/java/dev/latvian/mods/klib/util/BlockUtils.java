package dev.latvian.mods.klib.util;

import dev.latvian.mods.klib.block.ConnectedBlock;
import dev.latvian.mods.klib.block.PositionedBlock;
import dev.latvian.mods.klib.block.filter.BlockFilter;
import dev.latvian.mods.klib.core.KLibBlockState;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
import it.unimi.dsi.fastutil.objects.Reference2FloatOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.List;
import java.util.function.Predicate;

public interface BlockUtils {
	Reference2FloatMap<BlockState> DENSITY = new Reference2FloatOpenHashMap<>();

	static float getDensity(BlockState state) {
		if (state instanceof KLibBlockState klib) {
			return klib.klib$getDensity();
		}

		return DENSITY.computeIfAbsent(state, BlockUtils::computeDensity);
	}

	@ApiStatus.Internal
	static float computeDensity(BlockState state) {
		var b = state.getBlock();

		if (state.isAir() || b instanceof LightBlock || b instanceof BarrierBlock || b instanceof FireBlock) {
			return 0F;
		} else if (b instanceof CarpetBlock || b instanceof ButtonBlock || b instanceof PressurePlateBlock || b instanceof VineBlock || b instanceof LadderBlock) {
			return 0.06125F;
		} else if (b instanceof DoorBlock || b instanceof SnowLayerBlock || b instanceof FlowerPotBlock) {
			return 0.125F;
		} else if (b instanceof SlabBlock || b instanceof CrossCollisionBlock || b instanceof FenceGateBlock || b instanceof EnchantingTableBlock) {
			return 0.5F;
		} else if (b instanceof VegetationBlock) {
			return 0.25F;
		} else if (b instanceof SimpleWaterloggedBlock || b instanceof HopperBlock) {
			return 0.75F;
		} else {
			return 1F;
		}
	}

	static boolean isVisible(BlockState state) {
		if (state instanceof KLibBlockState klib) {
			return klib.klib$isVisible();
		}

		return computeIsVisible(state);
	}

	@ApiStatus.Internal
	static boolean computeIsVisible(BlockState state) {
		return state.getRenderShape() != RenderShape.INVISIBLE || !state.getFluidState().isEmpty();
	}

	static boolean isPartial(BlockState state) {
		if (state instanceof KLibBlockState klib) {
			return klib.klib$isPartial();
		}

		return computeIsPartial(state);
	}

	@ApiStatus.Internal
	static boolean computeIsPartial(BlockState state) {
		var b = state.getBlock();

		if (b instanceof AirBlock || b instanceof HalfTransparentBlock || b instanceof SimpleWaterloggedBlock || !state.getFluidState().isEmpty()) {
			return true;
		}

		return !isVisible(state) || getDensity(state) < 1F;
	}

	static boolean isFluid(BlockState state) {
		return !state.isAir() && state.liquid();
	}

	static String toString(BlockState state) {
		var sb = new StringBuilder();
		sb.append(state.getBlock().builtInRegistryHolder().getKey().identifier());
		boolean first = true;

		for (var prop : state.getProperties()) {
			var value = state.getValue(prop);

			if (!value.equals(state.getBlock().defaultBlockState().getValue(prop))) {
				if (first) {
					sb.append('[');
					first = false;
				} else {
					sb.append(',');
				}

				sb.append(prop.getName());
				sb.append('=');
				sb.append(prop.getName(Cast.to(value)));
			}
		}

		if (!first) {
			sb.append(']');
		}

		return sb.toString();
	}

	static void walkBlocks(LevelReader level, ConnectedBlock.WalkType walkType, BlockPos start, @Nullable BlockFilter filter, boolean onlyExposed, int maxDistance, Predicate<ConnectedBlock> callback) {
		if (filter == BlockFilter.ANY.value()) {
			filter = null;
		}

		var traversed = new LongOpenHashSet();
		var queue = new ArrayDeque<ConnectedBlock>();
		var partialCache = new Long2IntOpenHashMap();
		partialCache.defaultReturnValue(-1);
		var partialMutablePos = new BlockPos.MutableBlockPos();

		queue.add(new ConnectedBlock(new PositionedBlock(start, level.getBlockState(start)), 0));
		traversed.add(start.asLong());

		while (!queue.isEmpty()) {
			var c = queue.pop();

			if (c.distance() == 0 || (filter == null ? !c.block().state().isAir() : filter.test(level, c.block().pos(), c.block().state()))) {
				if (onlyExposed && !isBlockExposed(level, partialCache, c.block().pos().getX(), c.block().pos().getY(), c.block().pos().getZ(), partialMutablePos)) {
					continue;
				}

				if (callback.test(c)) {
					break;
				}

				if (c.distance() + 1 > maxDistance) {
					continue;
				}

				for (var o : walkType.offsets) {
					var offset = c.block().pos().offset(o);
					var state = level.getBlockState(offset);

					if (!state.isAir() && traversed.add(offset.asLong())) {
						queue.add(new ConnectedBlock(new PositionedBlock(offset, state), c.distance() + 1));
					}
				}
			}
		}
	}

	static List<ConnectedBlock> walkBlocks(LevelReader level, ConnectedBlock.WalkType walkType, BlockPos start, @Nullable BlockFilter filter, boolean onlyExposed, int maxDistance, int maxTotalBlocks) {
		var result = new Long2ObjectOpenHashMap<ConnectedBlock>();

		walkBlocks(level, walkType, start, filter, onlyExposed, maxDistance, c -> {
			result.put(c.block().pos().asLong(), c);
			return result.size() >= maxTotalBlocks;
		});

		return List.copyOf(result.values());
	}

	static boolean isBlockPartial(LevelReader level, Long2IntOpenHashMap cache, BlockPos pos) {
		long key = pos.asLong();
		int exposed = cache.get(key);

		if (exposed == -1) {
			exposed = isPartial(level.getBlockState(pos)) ? 1 : 0;
			cache.put(key, exposed);
		}

		return exposed == 1;
	}

	static boolean isBlockExposed(LevelReader level, Long2IntOpenHashMap cache, int x, int y, int z, BlockPos.MutableBlockPos mutable) {
		return isBlockPartial(level, cache, mutable.set(x, y + 1, z))
			|| isBlockPartial(level, cache, mutable.set(x, y - 1, z))
			|| isBlockPartial(level, cache, mutable.set(x - 1, y, z))
			|| isBlockPartial(level, cache, mutable.set(x + 1, y, z))
			|| isBlockPartial(level, cache, mutable.set(x, y, z - 1))
			|| isBlockPartial(level, cache, mutable.set(x, y, z + 1));
	}

	static boolean isBlockExposed(LevelReader level, int x, int y, int z, BlockPos.MutableBlockPos mutablePos) {
		return isPartial(level.getBlockState(mutablePos.set(x, y + 1, z)))
			|| isPartial(level.getBlockState(mutablePos.set(x, y - 1, z)))
			|| isPartial(level.getBlockState(mutablePos.set(x - 1, y, z)))
			|| isPartial(level.getBlockState(mutablePos.set(x + 1, y, z)))
			|| isPartial(level.getBlockState(mutablePos.set(x, y, z - 1)))
			|| isPartial(level.getBlockState(mutablePos.set(x, y, z + 1)));
	}

	static boolean isBlockExposed(LevelReader level, BlockPos pos, BlockState state) {
		return !state.isAir() && isBlockExposed(level, pos.getX(), pos.getY(), pos.getZ(), new BlockPos.MutableBlockPos());
	}

	static int getPackedLight(LevelReader level, BlockPos pos) {
		int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
		int skyLight = level.getBrightness(LightLayer.SKY, pos);
		return blockLight << 4 | skyLight << 20;
	}

	static double getGroundY(BlockGetter level, double x, double y, double z) {
		var bpos = new BlockPos.MutableBlockPos(x, y + 0.001D, z);
		BlockState state;

		while ((state = level.getBlockState(bpos)).isAir()) {
			bpos.move(0, -1, 0);

			if (level.isOutsideBuildHeight(bpos.getY())) {
				return Double.NaN;
			}
		}

		return bpos.getY() + state.getCollisionShape(level, bpos).max(Direction.Axis.Y);
	}
}
