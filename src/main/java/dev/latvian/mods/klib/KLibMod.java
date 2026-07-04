package dev.latvian.mods.klib;

import dev.latvian.mods.klib.command.KLibCommandArgumentTypes;
import dev.latvian.mods.klib.net.SyncCustomRegistryMetaPayload;
import dev.latvian.mods.klib.net.SyncCustomRegistryValuesPayload;
import dev.latvian.mods.klib.platform.NeoPlatformHelper;
import dev.latvian.mods.klib.platform.PlatformHelper;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.util.Cast;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Function;
import java.util.stream.Collectors;

@Mod(KLib.ID)
@EventBusSubscriber(modid = KLib.ID)
public class KLibMod {
	public KLibMod(ModContainer mod, IEventBus bus) {
		PlatformHelper.CURRENT = new NeoPlatformHelper(mod);
		KLib.VERSION = mod.getModInfo().getVersion().toString();
		KLibCommandArgumentTypes.REGISTRY.register(bus);
	}

	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event) {
		KLib.setup();
	}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		KLibCommands.register(event.getDispatcher(), event.getBuildContext());
	}

	@SubscribeEvent
	public static void serverStarting(ServerStartingEvent event) {
		CustomRegistry.buildAllMeta();
	}

	@SubscribeEvent
	public static void syncDataPacks(OnDatapackSyncEvent event) {
		event.getRelevantPlayers().forEach(CustomRegistry::syncAll);
	}

	@SubscribeEvent
	public static void registerPackets(RegisterPayloadHandlersEvent event) {
		var registrar = event.registrar("1");
		registrar.playToClient(SyncCustomRegistryMetaPayload.TYPE, SyncCustomRegistryMetaPayload.STREAM_CODEC, KLibMod::handleServerRegistryMeta);
		registrar.playToClient(SyncCustomRegistryValuesPayload.TYPE, SyncCustomRegistryValuesPayload.STREAM_CODEC, KLibMod::handleServerRegistryValues);
	}

	private static void handleServerRegistryMeta(SyncCustomRegistryMetaPayload payload, IPayloadContext context) {
		var map = payload.registries().stream().collect(Collectors.toMap(info -> info.registryKeys().root(), Function.identity()));

		for (var registry : CustomRegistry.ALL.values()) {
			if (registry.syncValues()) {
				var info = map.get(registry.registryKeys().root());
				registry.readMeta(Cast.to(info));
			}
		}
	}

	private static void handleServerRegistryValues(SyncCustomRegistryValuesPayload payload, IPayloadContext context) {
		var registry = CustomRegistry.ALL.get(payload.info().registryKeys().root());

		if (registry != null) {
			var registryAccess = context.player().registryAccess();
			var platformType = PlatformHelper.CURRENT.getPlatformOf(context.player());
			registry.readValues(Cast.to(payload.info()), registryAccess, platformType);
		}
	}
}
