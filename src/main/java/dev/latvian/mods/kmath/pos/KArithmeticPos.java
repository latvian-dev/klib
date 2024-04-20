package dev.latvian.mods.kmath.pos;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public record KArithmeticPos(Operation op, KPos kposA, KPos kposB) implements KPos {
	public enum Operation {
		ADD,
		SUB,
		MUL,
		DIV,
		MOD,
		POW,
		MIN,
		MAX;

		@Nullable
		public static Operation get(String op) {
			return switch (op) {
				case "add", "+" -> ADD;
				case "sub", "-" -> SUB;
				case "mul", "*" -> MUL;
				case "div", "/" -> DIV;
				case "mod", "%" -> MOD;
				case "pow", "**" -> POW;
				case "min" -> MIN;
				case "max" -> MAX;
				default -> null;
			};
		}
	}

	@Override
	public void tick(Vector3d pos) {
		kposB.tick(pos);
		var x = pos.x;
		var y = pos.y;
		var z = pos.z;
		kposA.tick(pos);

		switch (op) {
			case ADD -> pos.add(x, y, z);
			case SUB -> pos.sub(x, y, z);
			case MUL -> pos.mul(x, y, z);
			case DIV -> pos.div(x, y, z);
			case MOD -> {
				pos.x = x == 0D ? pos.x : (pos.x % x);
				pos.y = y == 0D ? pos.y : (pos.y % y);
				pos.z = z == 0D ? pos.z : (pos.z % z);
			}
			case POW -> {
				pos.x = Math.pow(x, pos.x);
				pos.y = Math.pow(y, pos.y);
				pos.z = Math.pow(z, pos.z);
			}
			case MIN -> {
				pos.x = Math.min(x, pos.x);
				pos.y = Math.min(y, pos.y);
				pos.z = Math.min(z, pos.z);
			}
			case MAX -> {
				pos.x = Math.max(x, pos.x);
				pos.y = Math.max(y, pos.y);
				pos.z = Math.max(z, pos.z);
			}
		}
	}
}
