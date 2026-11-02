package dev.latvian.mods.klib.icon.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.klib.icon.ItemIcon;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

public class ItemIconRenderState extends IconRenderState {
	public static void draw(ItemIcon icon, IconGuiExtractContext ctx) {
		ctx.graphics().fakeItem(icon.stack(), ctx.x() - 8, ctx.y() - 8);
	}

	public ItemStackRenderState stackRenderState;
	public int outlineColor;

	@Nullable
	public static IconRenderState extract(ItemIcon icon, ItemIconRenderState state, IconWorldExactContext ctx) {
		if (icon.stack().isEmpty()) {
			return null;
		}

		if (state.stackRenderState == null) {
			state.stackRenderState = new ItemStackRenderState();
		}

		ctx.mc().getItemModelResolver().updateForTopItem(state.stackRenderState, icon.stack(), ItemDisplayContext.GROUND, ctx.mc().level, ctx.itemOwner(), 0);

		if (state.stackRenderState.isEmpty()) {
			return null;
		}

		state.outlineColor = 0;
		return state;
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
