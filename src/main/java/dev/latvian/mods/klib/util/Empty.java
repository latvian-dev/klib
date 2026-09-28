package dev.latvian.mods.klib.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface Empty {
	Object[] OBJECT_ARRAY = new Object[0];
	String[] STRING_ARRAY = new String[0];
	ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");
	CompoundTag COMPOUND_TAG = new CompoundTag();
	Component COMPONENT = Component.empty();
	GameProfile PROFILE = new GameProfile(Util.NIL_UUID, "");
	Runnable RUNNABLE = () -> {
	};

	static boolean isEmpty(Component component) {
		return component == null || component == COMPONENT || component.getString().isEmpty();
	}
}
