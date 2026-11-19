package dev.latvian.mods.klib.net;

import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryMetaInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.network.ConfigurationTask;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SyncCustomRegistriesTask implements ConfigurationTask {
	public static final Type TYPE = new Type(KLib.ID + ":sync_custom_registries");

	@Override
	public void start(Consumer<Packet<?>> connection) {
		var metaList = new ArrayList<CustomRegistryMetaInfo>();

		for (var registry : CustomRegistry.ALL.values()) {
			metaList.add(registry.writeMeta());
		}

		connection.accept(new ClientboundCustomPayloadPacket(new SyncCustomRegistryMetaPayload(metaList)));
	}

	@Override
	public Type type() {
		return TYPE;
	}
}
