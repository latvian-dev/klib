package dev.latvian.mods.klib.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.ClientAsset;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.players.NameAndId;
import net.minecraft.util.Util;

public interface Empty {
	Object[] OBJECT_ARRAY = new Object[0];
	String[] STRING_ARRAY = new String[0];
	Identifier ID = Identifier.withDefaultNamespace("empty");
	ClientAsset.ResourceTexture TEXTURE = new ClientAsset.ResourceTexture(Identifier.withDefaultNamespace("misc/white"));
	CompoundTag COMPOUND_TAG = new CompoundTag();
	Component COMPONENT = Component.empty();
	NameAndId NAME_AND_ID = new NameAndId(Util.NIL_UUID, "");
	GameProfile PROFILE = new GameProfile(Util.NIL_UUID, "");
	Runnable RUNNABLE = () -> {
	};

	static boolean isEmpty(Component component) {
		return component == null || component == COMPONENT || component.getString().isEmpty();
	}
}
