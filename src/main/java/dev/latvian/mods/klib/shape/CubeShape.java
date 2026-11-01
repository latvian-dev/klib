package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.math.AAFBB;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

public record CubeShape(float size, AAFBB box) implements Shape {
	public static final UnitType<ByteBuf, Shape> UNIT_CUBE = UnitType.create("unit_cube", new CubeShape(1F));

	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"cube",
		"size",
		Codec.FLOAT,
		ByteBufCodecs.FLOAT,
		CubeShape::new,
		CubeShape::size
	);

	public CubeShape(float size) {
		this(size, AAFBB.centered(size));
	}

	@Override
	public DynamicType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public Ref<Shape> optimize(Ref<Shape> self) {
		return size == 1F ? UNIT_CUBE : self;
	}
}
