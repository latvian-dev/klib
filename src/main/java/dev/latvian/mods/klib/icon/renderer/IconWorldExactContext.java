package dev.latvian.mods.klib.icon.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record IconWorldExactContext(Minecraft mc, Vec3 cameraPos, float delta, int alpha, @Nullable ItemOwner itemOwner) {
}
