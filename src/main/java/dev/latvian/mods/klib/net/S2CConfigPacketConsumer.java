package dev.latvian.mods.klib.net;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ClientConfigurationPacketListener;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface S2CConfigPacketConsumer {
	void s2c(@Nullable Packet<? super ClientConfigurationPacketListener> packet);

	default void s2c(CustomPacketPayload packet) {
		s2c(new ClientboundCustomPayloadPacket(packet));
	}
}
