package dev.latvian.mods.klib.icon.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public record IconGuiExtractContext(Minecraft mc, GuiGraphicsExtractor graphics, int x, int y, int alpha) {
}