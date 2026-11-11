package dev.latvian.mods.klib.math;

import net.minecraft.core.Direction;

public interface Directions {
	Direction[] ALL = Direction.values();
	Direction[] HORIZONTAL = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
	Direction[] VERTICAL = {Direction.DOWN, Direction.UP};
}