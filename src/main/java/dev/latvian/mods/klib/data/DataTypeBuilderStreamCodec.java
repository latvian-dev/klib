package dev.latvian.mods.klib.data;

import dev.latvian.mods.klib.util.Cast;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.function.Function;

public record DataTypeBuilderStreamCodec<C>(List<DataTypeField<C, ?>> fields, Function<Object[], C> constructor, boolean canBeOptional) implements StreamCodec<ByteBuf, C> {
	public DataTypeBuilderStreamCodec(List<DataTypeField<C, ?>> fields, Function<Object[], C> constructor) {
		this(fields, constructor, fields.stream().anyMatch(DataTypeField::canBeOptional));
	}

	@Override
	public C decode(ByteBuf buf) {
		int o = canBeOptional ? VarInt.read(buf) : 0xFFFFFFFF;
		var args = new Object[fields.size()];

		for (int i = 0; i < fields.size(); i++) {
			var c = fields.get(i);
			args[i] = c.decode(buf, (o & (1 << i)) != 0);
		}

		return constructor.apply(args);
	}

	@Override
	public void encode(ByteBuf buf, C value) {
		var args = new Object[fields.size()];
		int o = 0;

		for (int i = 0; i < fields.size(); i++) {
			var c = fields.get(i);
			args[i] = c.get(value);

			if (c.shouldEncode(Cast.to(args[i]))) {
				o |= (1 << i);
			}
		}

		if (canBeOptional) {
			VarInt.write(buf, o);
		}

		for (int i = 0; i < fields.size(); i++) {
			if ((o & (1 << i)) != 0) {
				fields.get(i).encode(buf, Cast.to(args[i]));
			}
		}
	}
}
