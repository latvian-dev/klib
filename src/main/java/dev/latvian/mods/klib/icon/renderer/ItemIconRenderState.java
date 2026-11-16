package dev.latvian.mods.klib.icon.renderer;

import dev.latvian.mods.klib.icon.ItemIcon;
import dev.latvian.mods.klib.render.SubmitContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemIconRenderState extends IconRenderState {
	public static void draw(ItemIcon icon, IconGuiExtractContext ctx) {
		ctx.graphics().fakeItem(icon.stack(), ctx.x() - 8, ctx.y() - 8);
	}

	public ItemStackRenderState stackRenderState;
	public int outlineColor;

	public static IconRenderState extract(Minecraft mc, ItemStack stack, ItemIconRenderState state, @Nullable ItemOwner itemOwner) {
		if (stack.isEmpty()) {
			return null;
		}

		if (state.stackRenderState == null) {
			state.stackRenderState = new ItemStackRenderState();
		}

		mc.getItemModelResolver().updateForTopItem(state.stackRenderState, stack, ItemDisplayContext.GROUND, mc.level, itemOwner, 0);

		if (state.stackRenderState.isEmpty()) {
			return null;
		}

		state.outlineColor = 0;
		return state;
	}

	@Nullable
	public static IconRenderState extract(ItemIcon icon, ItemIconRenderState state, IconWorldExactContext ctx) {
		return extract(ctx.mc(), icon.stack(), state, ctx.itemOwner());
	}

	@Override
	public void submit(SubmitContext ctx) {
		ctx.push(x, y - 0.25D, z);
		ctx.rotate(rotation);
		ctx.scale(scale * 1.75F);
		stackRenderState.submit(ctx.poseStack(), ctx.collector(), light, overlay, outlineColor);
		ctx.pop();
	}
}
