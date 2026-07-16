package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibCamera;
import dev.latvian.mods.klib.math.Line;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public abstract class CameraMixin implements KLibCamera {
	@Shadow
	@Final
	private Vector3f forwards;

	@Shadow
	public abstract Vec3 position();

	@Override
	@Invoker("setPosition")
	public abstract void klib$setPosition(Vec3 pos);

	@Override
	public Line klib$ray(double distance) {
		var start = position();
		var end = start.add(forwards.x * distance, forwards.y * distance, forwards.z * distance);
		return new Line(start, end);
	}
}
