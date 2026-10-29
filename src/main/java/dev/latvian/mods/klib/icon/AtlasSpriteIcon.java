package dev.latvian.mods.klib.icon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.network.codec.ByteBufCodecs;

public record AtlasSpriteIcon(AtlasSprite sprite, boolean translucent, Color tint) implements Icon {
	public static final DynamicType<RegistryFriendlyByteBuf, Icon> TYPE = DynamicType.create(
		"atlas_sprite",
		RecordCodecBuilder.mapCodec(i -> i.group(
			MCCodecs.ATLAS_SPRITE.fieldOf("sprite").forGetter(AtlasSpriteIcon::sprite),
			Codec.BOOL.optionalFieldOf("translucent", false).forGetter(AtlasSpriteIcon::translucent),
			Color.CODEC.optionalFieldOf("tint", Color.WHITE).forGetter(AtlasSpriteIcon::tint)
		).apply(i, AtlasSpriteIcon::new)),
		CompositeStreamCodec.of(
			MCStreamCodecs.ATLAS_SPRITE, AtlasSpriteIcon::sprite,
			ByteBufCodecs.BOOL, AtlasSpriteIcon::translucent,
			KLibStreamCodecs.optional(Color.STREAM_CODEC, Color.WHITE), AtlasSpriteIcon::tint,
			AtlasSpriteIcon::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, Icon> type() {
		return TYPE;
	}
}
