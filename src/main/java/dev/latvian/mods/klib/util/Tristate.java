package dev.latvian.mods.klib.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.BooleanSupplier;

public enum Tristate implements StringRepresentable {
	FALSE("false"),
	TRUE("true"),
	DEFAULT("default");

	public static final Tristate[] VALUES = values();

	public static final Codec<Tristate> CODEC = Codec.either(Codec.BOOL, Codec.unit("default")).xmap(
		either -> either.map(b -> b ? TRUE : FALSE, s -> s.equalsIgnoreCase("true") ? TRUE : s.equalsIgnoreCase("false") ? FALSE : DEFAULT),
		t -> t == DEFAULT ? Either.right("default") : Either.left(t == TRUE)
	);

	public static final StreamCodec<ByteBuf, Tristate> STREAM_CODEC = KLibStreamCodecs.enumValue(VALUES);

	public static Tristate wrap(Object from) {
		return switch (from) {
			case null -> DEFAULT;
			case Tristate t -> t;
			case Boolean b -> b ? TRUE : FALSE;
			default -> switch (from.toString().toLowerCase(Locale.ROOT)) {
				case "true" -> TRUE;
				case "false" -> FALSE;
				default -> DEFAULT;
			};
		};
	}

	public final String name;

	Tristate(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public boolean resolve(boolean defaultValue) {
		return this == DEFAULT ? defaultValue : this == TRUE;
	}

	public boolean resolve(BooleanSupplier defaultValue) {
		return this == DEFAULT ? defaultValue.getAsBoolean() : this == TRUE;
	}

	public boolean test(boolean enabled) {
		return this == DEFAULT || (this == TRUE) == enabled;
	}

	public boolean test(BooleanSupplier enabled) {
		return this == DEFAULT || (this == TRUE) == enabled.getAsBoolean();
	}
}
