package dev.latvian.mods.klib.core;

import com.mojang.blaze3d.platform.InputConstants;
import dev.latvian.mods.klib.entity.PlayerInput;
import dev.latvian.mods.klib.math.Line;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;

public interface KLibLocalPlayer extends KLibPlayer {
	@Override
	default LocalPlayer klib$self() {
		return (LocalPlayer) this;
	}

	@Override
	default Line klib$ray(double distance, float delta) {
		return Minecraft.getInstance().gameRenderer.getMainCamera().klib$ray(distance);
	}

	static PlayerInput fromInput(Minecraft mc, LocalPlayer player, boolean mouse) {
		var in = player.input.keyPresses;

		// Modifiers
		boolean shift = Screen.hasShiftDown();
		boolean control = Screen.hasControlDown();
		boolean alt = Screen.hasAltDown();
		boolean tab = InputConstants.isKeyDown(mc.getWindow().getWindow(), InputConstants.KEY_TAB);
		// Mouse
		boolean mouseLeft = mouse && mc.options.keyAttack.isDown();
		boolean mouseRight = mouse && mc.options.keyUse.isDown();
		boolean mouseMiddle = mouse && mc.options.keyPickItem.isDown();
		boolean mouseBack = false; // mouse && GLFW.glfwGetMouseButton(windowId, 3) == GLFW.GLFW_PRESS;
		boolean mouseNext = false; // mouse && GLFW.glfwGetMouseButton(windowId, 4) == GLFW.GLFW_PRESS;

		return PlayerInput.of(
			// Movement
			in.forward(), in.backward(), in.left(), in.right(), in.jump(), in.shift(), in.sprint(),
			// Modifiers
			shift, control, alt, tab,
			// Mouse
			mouseLeft, mouseRight, mouseMiddle, mouseBack, mouseNext
		);
	}
}
