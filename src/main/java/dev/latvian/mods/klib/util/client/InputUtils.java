package dev.latvian.mods.klib.util.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.latvian.mods.klib.client.KLibKeys;
import dev.latvian.mods.klib.entity.PlayerInput;
import net.minecraft.client.Minecraft;

public interface InputUtils {
	static PlayerInput fromInput(Minecraft mc) {
		var in = mc.player.input.keyPresses;

		// Modifiers
		boolean shift = mc.hasShiftDown();
		boolean control = mc.hasControlDown();
		boolean alt = mc.hasAltDown();
		boolean tab = InputConstants.isKeyDown(InputConstants.KEY_TAB);
		// Mouse
		boolean attack = mc.options.keyAttack.isDown();
		boolean use = mc.options.keyUse.isDown();
		boolean pick = mc.options.keyPickItem.isDown();
		boolean decrease = KLibKeys.decreaseKeyMapping.isDown();
		boolean increase = KLibKeys.increaseKeyMapping.isDown();

		return PlayerInput.of(
			// Movement
			in.forward(), in.backward(), in.left(), in.right(), in.jump(), in.shift(), in.sprint(),
			// Modifiers
			shift, control, alt, tab,
			// Mouse
			attack, use, pick, decrease, increase
		);
	}
}
