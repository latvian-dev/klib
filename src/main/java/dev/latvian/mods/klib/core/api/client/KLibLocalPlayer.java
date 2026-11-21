package dev.latvian.mods.klib.core.api.client;

import dev.latvian.mods.klib.net.C2SPacketConsumer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public interface KLibLocalPlayer extends C2SPacketConsumer {
	default LocalPlayer klib$self() {
		return (LocalPlayer) this;
	}

	@Override
	default void c2s(Packet<? super ServerGamePacketListener> packet) {
		klib$self().connection.send(packet);
	}
}
