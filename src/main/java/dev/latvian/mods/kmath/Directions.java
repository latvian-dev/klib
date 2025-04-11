package dev.latvian.mods.kmath;

import net.minecraft.core.Direction;

public interface Directions {
	Direction[] ALL = Direction.VALUES;
	Direction[] HORIZONTAL = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
	Direction[] VERTICAL = {Direction.DOWN, Direction.UP};
}