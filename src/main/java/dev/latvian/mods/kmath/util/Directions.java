package dev.latvian.mods.kmath.util;

import net.minecraft.util.math.Direction;

public interface Directions {
	Direction[] ALL = Direction.values();
	Direction[] HORIZONTAL = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
	Direction[] VERTICAL = {Direction.DOWN, Direction.UP};
}
