package dev.latvian.mods.klib.visual.renderer;

import dev.latvian.mods.klib.knumber.KNumberContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public record VisualExactContext(Minecraft mc, Vec3 cameraPos, float delta, KNumberContext numberContext) {
}
