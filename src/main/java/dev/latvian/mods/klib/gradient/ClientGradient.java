package dev.latvian.mods.klib.gradient;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.color.PositionedColor;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.DynamicType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ClientGradient(ClientGradientRef gradient) implements Gradient {
	public static final class ClientGradientRef {
		public static final Map<Identifier, ClientGradientRef> INTERN = new HashMap<>();

		public static ClientGradientRef intern(Identifier id) {
			return INTERN.computeIfAbsent(id, ClientGradientRef::new);
		}

		private static final Codec<ClientGradientRef> CODEC = KLibCodecs.commonIdentifier("vidlib").xmap(ClientGradientRef::intern, ClientGradientRef::identifier);
		private static final StreamCodec<ByteBuf, ClientGradientRef> STREAM_CODEC = KLibStreamCodecs.commonIdentifier("vidlib").map(ClientGradientRef::intern, ClientGradientRef::identifier);

		private final Identifier identifier;
		private Gradient gradient;

		private ClientGradientRef(Identifier identifier) {
			this.identifier = identifier;
			this.gradient = Gradient.EMPTY.value();
		}

		public Identifier identifier() {
			return identifier;
		}

		public String toString() {
			return identifier.toString();
		}
	}

	public static final DynamicType<ByteBuf, Gradient> TYPE = DynamicType.create(
		"client",
		"gradient",
		ClientGradientRef.CODEC,
		ClientGradientRef.STREAM_CODEC,
		ClientGradient::new,
		ClientGradient::gradient
	);

	public static void updateRefs(Map<Identifier, Gradient> map) {
		var empty = Gradient.EMPTY.value();

		for (var ref : ClientGradientRef.INTERN.values()) {
			ref.gradient = empty;
		}

		for (var entry : map.entrySet()) {
			var ref = ClientGradientRef.intern(entry.getKey());
			ref.gradient = entry.getValue();
		}
	}

	@Override
	public CustomRegistryType<ByteBuf, Gradient> type() {
		return TYPE;
	}

	@Override
	public Color get(float delta) {
		return gradient.gradient.get(delta);
	}

	@Override
	public Color sample(RandomSource random) {
		return gradient.gradient.sample(random);
	}

	@Override
	public List<PositionedColor> getPositionedColors() {
		return gradient.gradient.getPositionedColors();
	}
}
