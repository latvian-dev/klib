package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record VariableKNumber(String name) implements KNumber {
	public static final DynamicType<RegistryFriendlyByteBuf, KNumber> TYPE = DynamicType.create(
		"variable",
		"name",
		Codec.STRING,
		ByteBufCodecs.STRING_UTF8,
		VariableKNumber::new,
		VariableKNumber::name
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KNumber> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Double get(KNumberContext ctx) {
		var num = ctx.getNumber(name);
		return num == null ? null : num.value().get(ctx);
	}

	@Override
	@NotNull
	public String toString() {
		return "#" + name;
	}

	@Override
	public boolean isStringLiteral() {
		return true;
	}
}
