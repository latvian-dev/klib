package dev.latvian.mods.klib.net;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import org.jetbrains.annotations.Nullable;

public interface C2SPacketConsumer {
	void c2s(@Nullable Packet<? super ServerGamePacketListener> packet);

	default void c2s(CustomPacketPayload packet) {
		c2s(new ServerboundCustomPayloadPacket(packet));
	}
}
