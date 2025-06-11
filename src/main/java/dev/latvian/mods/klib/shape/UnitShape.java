package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3fc;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class UnitShape implements Shape {
	private static final Map<String, UnitShape> MAP = new HashMap<>();
	static final Map<Shape, UnitShape> REF_MAP = new IdentityHashMap<>();

	public static void add(String name, Shape shape) {
		var unitShape = new UnitShape();
		unitShape.type = new ShapeType(name, MapCodec.unit(unitShape), StreamCodec.unit(unitShape));
		unitShape.shape = shape;
		MAP.put(name, unitShape);
		REF_MAP.put(shape, unitShape);
	}

	static {
		add("empty", EmptyShape.INSTANCE);
		add("cube", CubeShape.UNIT);
		add("square", CuboidShape.SQUARE_UNIT);
		add("sphere", SphereShape.UNIT);
		add("cylinder", CylinderShape.UNIT);
		add("circle", CircleShape.UNIT);
		add("line_down", LineShape.DOWN_UNIT);
		add("line_up", LineShape.UP_UNIT);
		add("line_north", LineShape.NORTH_UNIT);
		add("line_south", LineShape.SOUTH_UNIT);
		add("line_west", LineShape.WEST_UNIT);
		add("line_east", LineShape.EAST_UNIT);
	}

	public static final Codec<Shape> CODEC = Codec.STRING.flatXmap(s -> {
		var shape = MAP.get(s);
		return shape == null ? DataResult.error(() -> "Shape not found") : DataResult.success(shape);
	}, s -> {
		if (s instanceof UnitShape us) {
			return DataResult.success(us.type.name());
		} else if (REF_MAP.get(s) instanceof UnitShape us) {
			return DataResult.success(us.type.name());
		} else {
			return DataResult.error(() -> "Shape not found");
		}
	});

	public static final StreamCodec<ByteBuf, UnitShape> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(MAP::get, s -> s.type.name());

	private ShapeType type;
	private Shape shape;

	private UnitShape() {
	}

	@Override
	public ShapeType type() {
		return type;
	}

	@Override
	public Shape optimize() {
		return shape.optimize();
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
		shape.buildLines(x, y, z, callback);
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		shape.buildQuads(x, y, z, callback);
	}

	@Override
	public boolean contains(Vector3fc p) {
		return shape.contains(p);
	}

	@Override
	public boolean isVisible(double x, double y, double z, FrustumCheck frustum) {
		return shape.isVisible(x, y, z, frustum);
	}

	@Override
	public String toString() {
		return type.name() + "[" + shape + "]";
	}
}
