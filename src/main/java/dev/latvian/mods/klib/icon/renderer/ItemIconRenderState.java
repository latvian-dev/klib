package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.icon.ItemIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ItemIconRenderState extends IconRenderState<ItemIcon> {
	public ItemStack stack;
	public ItemStackRenderState stackRenderState;

	@Override
	public void extract(ItemIcon icon, float delta) {
		stack = icon.stack();

		if (stackRenderState == null) {
			stackRenderState = new ItemStackRenderState();
		}

		var mc = Minecraft.getInstance();
		mc.getItemModelResolver().updateForTopItem(stackRenderState, stack, ItemDisplayContext.GROUND, mc.level, null, 0);
	}

	@Override
	public void draw(GuiContext ctx) {
		ctx.graphics().fakeItem(stack, -8, -8);
	}

	@Override
	public void submit(WorldContext ctx) {
		var ms = ctx.poseStack();
		ms.pushPose();
		ms.translate(0F, -0.25F, 0F);
		ms.scale(1.75F, 1.75F, 1.75F);
		stackRenderState.submit(ms, ctx.submitNodeCollector(), ctx.light(), ctx.overlay(), 0);
		ms.popPose();
	}
}
