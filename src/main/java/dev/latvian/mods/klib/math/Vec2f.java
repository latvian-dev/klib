package dev.latvian.mods.klib.math;

public record Vec2f(float x, float y) {
	public static final Vec2f ZERO = new Vec2f(0F, 0F);
	public static final Vec2f ONE = new Vec2f(1F, 1F);
	public static final Vec2f UNIT_X = new Vec2f(1F, 0F);
	public static final Vec2f NEG_UNIT_X = new Vec2f(-1F, 0F);
	public static final Vec2f UNIT_Y = new Vec2f(0F, 1F);
	public static final Vec2f NEG_UNIT_Y = new Vec2f(0F, -1F);
	public static final Vec2f MAX = new Vec2f(Float.MAX_VALUE, Float.MAX_VALUE);
	public static final Vec2f MIN = new Vec2f(Float.MIN_VALUE, Float.MIN_VALUE);

	public Vec2f scale(float factor) {
		return new Vec2f(this.x * factor, this.y * factor);
	}

	public double dot(Vec2f other) {
		return this.x * other.x + this.y * other.y;
	}

	public Vec2f add(Vec2f other) {
		return new Vec2f(this.x + other.x, this.y + other.y);
	}

	public Vec2f add(float value) {
		return new Vec2f(this.x + value, this.y + value);
	}

	public boolean equals(Vec2f other) {
		return this.x == other.x && this.y == other.y;
	}

	public Vec2f normalized() {
		double f = Math.sqrt(this.x * this.x + this.y * this.y);
		return f < 1.0E-4D ? ZERO : new Vec2f((float) (this.x / f), (float) (this.y / f));
	}

	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	public float lengthSquared() {
		return this.x * this.x + this.y * this.y;
	}

	public double distanceToSqr(Vec2f other) {
		var dx = other.x - this.x;
		var dy = other.y - this.y;
		return dx * dx + dy * dy;
	}

	public Vec2f negated() {
		return new Vec2f(-this.x, -this.y);
	}

	public Vec2f lerp(Vec2f to, float delta) {
		if (delta == 0F || equals(to)) {
			return this;
		} else if (delta == 1F) {
			return to;
		} else {
			return new Vec2f(this.x + (to.x - this.x) * delta, this.y + (to.y - this.y) * delta);
		}
	}
}
