package dev.latvian.mods.klib.icon.renderer;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public record GuiExtractContext(GuiGraphicsExtractor graphics, int x, int y, int alpha) {
}