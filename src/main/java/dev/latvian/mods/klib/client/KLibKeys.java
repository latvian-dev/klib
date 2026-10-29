package dev.latvian.mods.klib.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.latvian.mods.klib.util.ID;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.sdl.SDLScancode;

public class KLibKeys {
	public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(ID.klib("klib"));

	public static KeyMapping increaseKeyMapping;
	public static KeyMapping decreaseKeyMapping;

	private static KeyMapping register(RegisterKeyMappingsEvent event, String name, KeyModifier modifier, int defaultKey, KeyConflictContext conflict) {
		var key = new KeyMapping(name, conflict, modifier, InputConstants.Type.KEYBOARD.getOrCreate(defaultKey), CATEGORY);
		event.register(key);
		return key;
	}

	private static KeyMapping register(RegisterKeyMappingsEvent event, String name, KeyModifier modifier, int defaultKey) {
		return register(event, name, modifier, defaultKey, KeyConflictContext.IN_GAME);
	}

	public static void register(RegisterKeyMappingsEvent event) {
		event.registerCategory(CATEGORY);

		increaseKeyMapping = register(event, "key.klib.increase", KeyModifier.NONE, SDLScancode.SDL_SCANCODE_EQUALS);
		decreaseKeyMapping = register(event, "key.klib.decrease", KeyModifier.NONE, SDLScancode.SDL_SCANCODE_MINUS);
	}
}



