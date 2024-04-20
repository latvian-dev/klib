package dev.latvian.mods.kmath.util;

import dev.latvian.mods.kmath.KMath;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;

public record FloatPair(float a, float b) {
	public static final FloatPair ZERO = new FloatPair(0F, 0F);
	public static final FloatPair ONE = new FloatPair(1F, 1F);

	public static FloatPair of(float a, float b) {
		return a == 0F && b == 0F ? ZERO : a == 1F && b == 1F ? ONE : Math.abs(a - b) < 0.0001F ? new FloatPair(a, a) : new FloatPair(a, b);
	}

	public static FloatPair of(float value) {
		return of(value, value);
	}

	public static FloatPair of(NbtElement nbt) {
		if (nbt instanceof NbtList list) {
			return of(list.getFloat(0), list.getFloat(1));
		} else if (nbt instanceof AbstractNbtNumber num) {
			return of(num.floatValue());
		} else if (nbt instanceof NbtIntArray arr) {
			return of(arr.get(0).floatValue(), arr.get(1).floatValue());
		} else if (nbt instanceof NbtByteArray arr) {
			return of(arr.get(0).floatValue(), arr.get(1).floatValue());
		} else {
			return null;
		}
	}

	public static NbtElement toNbt(float a, float b) {
		if (a == b) {
			return KMath.efficient(a);
		}

		var mn = KMath.efficient(a);
		var mx = KMath.efficient(b);

		if (mn.getType() == mx.getType()) {
			if (mn.getType() == NbtElement.BYTE_TYPE) {
				return new NbtByteArray(new byte[]{(byte) a, (byte) b});
			} else if (mn.getType() == NbtElement.INT_TYPE || mn.getType() == NbtElement.SHORT_TYPE) {
				return new NbtIntArray(new int[]{(int) a, (int) b});
			} else {
				var list = new NbtList();
				list.add(mn);
				list.add(mx);
				return list;
			}
		} else {
			var list = new NbtList();
			list.add(NbtFloat.of(a));
			list.add(NbtFloat.of(b));
			return list;
		}
	}

	public NbtElement toNbt() {
		return toNbt(a, b);
	}

	@Override
	public String toString() {
		return a == b ? KMath.format(a) : (KMath.format(a) + " & " + KMath.format(b));
	}
}
