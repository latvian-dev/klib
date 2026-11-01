package dev.latvian.mods.klib.icon.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.klib.icon.ItemIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;

public class ItemIconRenderState extends IconRenderState<ItemIcon> {
	public static void draw(ItemIcon icon, GuiExtractContext ctx) {
		ctx.graphics().fakeItem(icon.stack(), ctx.x() - 8, ctx.y() - 8);
	}

	public ItemStackRenderState stackRenderState;
	public int outlineColor;

	@Override
	public void extract(ItemIcon icon, WorldExactContext ctx) {
		if (stackRenderState == null) {
			stackRenderState = new ItemStackRenderState();
		}

		var mc = Minecraft.getInstance();
		mc.getItemModelResolver().updateForTopItem(stackRenderState, icon.stack(), ItemDisplayContext.GROUND, mc.level, null, 0);
		outlineColor = 0;
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector) {
		poseStack.pushPose();
		poseStack.translate(0F, -0.25F, 0F);
		poseStack.scale(1.75F, 1.75F, 1.75F);
		stackRenderState.submit(poseStack, collector, light, overlay, outlineColor);
		poseStack.popPose();
	}
}
