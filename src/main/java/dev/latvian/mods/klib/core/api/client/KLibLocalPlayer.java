package dev.latvian.mods.klib.core.api.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.latvian.mods.klib.client.KLibKeys;
import dev.latvian.mods.klib.core.api.common.KLibPlayer;
import dev.latvian.mods.klib.entity.PlayerInput;
import dev.latvian.mods.klib.math.Line;
import dev.latvian.mods.klib.net.C2SPacketConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public interface KLibLocalPlayer extends KLibPlayer, C2SPacketConsumer {
	@Override
	default LocalPlayer klib$self() {
		return (LocalPlayer) this;
	}

	@Override
	default void c2s(Packet<? super ServerGamePacketListener> packet) {
		klib$self().connection.send(packet);
	}

	@Override
	default Line klib$ray(double distance, float delta) {
		return Minecraft.getInstance().gameRenderer.mainCamera().klib$ray(distance);
	}

	static PlayerInput fromInput(Minecraft mc, LocalPlayer player) {
		var in = player.input.keyPresses;

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
