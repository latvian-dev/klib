package dev.latvian.mods.klib.io;

import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.util.StringRepresentable;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

public enum CompressionMethod implements StringRepresentable {
	NONE(0, "none", in -> in, out -> out),
	GZIP(1, "gzip", GZIPInputStream::new, GZIPOutputStream::new),
	DEFLATE(2, "deflate", InflaterInputStream::new, DeflaterOutputStream::new),
	LZ4(3, "lz4", LZ4BlockInputStream::new, LZ4BlockOutputStream::new),
	ZSTD(4, "zstd", ZstdInputStream::new, ZstdOutputStream::new),

	;

	public static final List<CompressionMethod> METHODS = List.of(values());

	public static CompressionMethod of(int id) {
		for (var method : METHODS) {
			if (method.id == id) {
				return method;
			}
		}

		throw new NullPointerException("Unknown compression method " + id);
	}

	public static final Codec<CompressionMethod> CODEC = KLibCodecs.anyEnumCodec(values());
	public static final StreamCodec<ByteBuf, CompressionMethod> STREAM_CODEC = ByteBufCodecs.idMapper(CompressionMethod::of, m -> m.id);
	public static final DataType<CompressionMethod> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, CompressionMethod.class);

	public final int id;
	public final String name;
	private final IOUnaryOperator<InputStream> input;
	private final IOUnaryOperator<OutputStream> output;

	CompressionMethod(int id, String name, IOUnaryOperator<InputStream> input, IOUnaryOperator<OutputStream> output) {
		this.id = id;
		this.name = name;
		this.input = input;
		this.output = output;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public InputStream in(InputStream in) throws IOException {
		return input.apply(in);
	}

	public OutputStream out(OutputStream out) throws IOException {
		return output.apply(out);
	}

	public InputStream bufferedIn(InputStream in) throws IOException {
		return new FastBufferedInputStream(in(in));
	}

	public OutputStream bufferedOut(OutputStream out) throws IOException {
		return new BufferedOutputStream(out(out));
	}
}
