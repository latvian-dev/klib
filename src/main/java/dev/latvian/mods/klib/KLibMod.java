package dev.latvian.mods.klib;

import dev.latvian.mods.klib.command.KLibCommandArgumentTypes;
import dev.latvian.mods.klib.core.KLibExtensions;
import dev.latvian.mods.klib.environment.KLibEnvironmentAttributes;
import dev.latvian.mods.klib.gradient.GradientImageLoader;
import dev.latvian.mods.klib.net.SyncCustomRegistriesTask;
import dev.latvian.mods.klib.net.SyncCustomRegistriesTaskResponsePayload;
import dev.latvian.mods.klib.net.SyncCustomRegistryMetaPayload;
import dev.latvian.mods.klib.net.SyncCustomRegistryValuesPayload;
import dev.latvian.mods.klib.particle.KLibParticles;
import dev.latvian.mods.klib.platform.NeoPlatformHelper;
import dev.latvian.mods.klib.platform.PlatformHelper;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryMetaInfo;
import dev.latvian.mods.klib.registry.RefType;
import dev.latvian.mods.klib.util.Cast;
import dev.latvian.mods.klib.util.ID;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Function;
import java.util.stream.Collectors;

@Mod(KLib.ID)
@EventBusSubscriber(modid = KLib.ID)
public class KLibMod {
	public KLibMod(ModContainer mod, IEventBus bus) {
		KLibExtensions.init();
		PlatformHelper.INSTANCE = new NeoPlatformHelper(mod);
		KLib.VERSION = mod.getModInfo().getVersion().toString();
		KLibCommandArgumentTypes.REGISTRY.register(bus);
		KLibParticles.init();
		KLibEnvironmentAttributes.init();
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
	public static void addReloadListeners(AddServerReloadListenersEvent event) {
		event.addListener(ID.klib("gradient"), new GradientImageLoader(RefType.SERVER));
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void syncDataPacks(OnDatapackSyncEvent event) {
		event.getRelevantPlayers().forEach(CustomRegistry::syncAll);
	}

	@SubscribeEvent
	public static void register(RegisterEvent event) {
		KLibBuiltInRegistries.registerAll(key -> event.getRegistryKey() == key);
	}

	@SubscribeEvent
	public static void registerPackets(RegisterPayloadHandlersEvent event) {
		var registrar = event.registrar("1");
		registrar.configurationToClient(SyncCustomRegistryMetaPayload.TYPE, SyncCustomRegistryMetaPayload.STREAM_CODEC, KLibMod::handleServerRegistryMeta);
		registrar.configurationToServer(SyncCustomRegistriesTaskResponsePayload.TYPE, SyncCustomRegistriesTaskResponsePayload.STREAM_CODEC, KLibMod::handleClientSyncCustomRegistriesTaskResponse);
		registrar.playToClient(SyncCustomRegistryValuesPayload.TYPE, SyncCustomRegistryValuesPayload.STREAM_CODEC, KLibMod::handleServerRegistryValues);
	}

	private static void handleServerRegistryMeta(SyncCustomRegistryMetaPayload payload, IPayloadContext context) {
		var map = payload.registries().stream().collect(Collectors.toMap(CustomRegistryMetaInfo::registryId, Function.identity()));

		for (var registry : CustomRegistry.ALL.values()) {
			var info = map.get(registry.registryId());
			registry.readMeta(Cast.to(info));
		}

		context.reply(SyncCustomRegistriesTaskResponsePayload.INSTANCE);
	}

	private static void handleServerRegistryValues(SyncCustomRegistryValuesPayload payload, IPayloadContext context) {
		var registry = CustomRegistry.ALL.get(payload.info().registryId().intern());

		if (registry != null) {
			var registryAccess = context.player().registryAccess();
			var platformType = PlatformHelper.INSTANCE.getPlatformOf(context.player());
			registry.readValues(Cast.to(payload.info()), registryAccess, platformType);
		}
	}

	private static void handleClientSyncCustomRegistriesTaskResponse(SyncCustomRegistriesTaskResponsePayload payload, IPayloadContext context) {
		KLib.LOGGER.info("Registries synced to " + ((ServerConfigurationPacketListenerImpl) context.listener()).getOwner().name());
		context.finishCurrentTask(SyncCustomRegistriesTask.TYPE);
	}

	@SubscribeEvent
	public static void registerConfigurationTasks(RegisterConfigurationTasksEvent event) {
		for (var task : KLibBuiltInRegistries.CONFIGURATION_TASKS) {
			event.register(task.get());
		}
	}
}
