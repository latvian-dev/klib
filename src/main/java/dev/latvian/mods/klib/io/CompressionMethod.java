package dev.latvian.mods.klib.io;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

public enum CompressionMethod {
	NONE(0, "none",
		in -> in,
		out -> out,
		bytes -> bytes,
		bytes -> bytes,
		buf -> buf,
		buf -> buf
	),

	GZIP(1, "gzip",
		GZIPInputStream::new,
		GZIPOutputStream::new
	),

	DEFLATE(2, "deflate",
		InflaterInputStream::new,
		DeflaterOutputStream::new
	),

	LZ4(3, "lz4",
		in -> LZ4BlockInputStream.newBuilder().build(in),
		LZ4BlockOutputStream::new
	),

	ZSTD(4, "zstd",
		ZstdInputStream::new,
		ZstdOutputStream::new,
		Zstd::compress,
		Zstd::decompress,
		buf -> Zstd.compress(IOUtils.toDirect(buf), Zstd.defaultCompressionLevel()),
		buf -> {
			int remaining = buf.remaining();
			return Zstd.decompress(IOUtils.toDirect(buf), remaining);
		}
	),

	;

	public static final List<CompressionMethod> METHODS = List.of(values());

	public static CompressionMethod of(int id) {
		if (id <= 0) {
			return NONE;
		}

		for (var method : METHODS) {
			if (method.id == id) {
				return method;
			}
		}

		throw new NullPointerException("Unknown compression method " + id);
	}

	public static CompressionMethod of(String name) {
		if (name.isEmpty()) {
			return NONE;
		}

		for (var method : METHODS) {
			if (method.name.equals(name)) {
				return method;
			}
		}

		throw new NullPointerException("Unknown compression method " + name);
	}

	public static final Codec<CompressionMethod> CODEC = KLibCodecs.anyEnum(values());
	public static final StreamCodec<ByteBuf, CompressionMethod> STREAM_CODEC = ByteBufCodecs.idMapper(CompressionMethod::of, m -> m.id);
	public static final DataType<CompressionMethod> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC);

	public final int id;
	public final String name;
	private final IOUnaryOperator<InputStream> input;
	private final IOUnaryOperator<OutputStream> output;
	private final IOUnaryOperator<byte[]> compressBytes;
	private final IOUnaryOperator<byte[]> decompressBytes;
	private final IOUnaryOperator<ByteBuffer> compressByteBuffer;
	private final IOUnaryOperator<ByteBuffer> decompressByteBuffer;

	CompressionMethod(
		int id, String name,
		IOUnaryOperator<InputStream> input,
		IOUnaryOperator<OutputStream> output,
		@Nullable IOUnaryOperator<byte[]> compressBytes,
		@Nullable IOUnaryOperator<byte[]> decompressBytes,
		@Nullable IOUnaryOperator<ByteBuffer> compressByteBuffer,
		@Nullable IOUnaryOperator<ByteBuffer> decompressByteBuffer
	) {
		this.id = id;
		this.name = name;
		this.input = input;
		this.output = output;
		this.compressBytes = compressBytes;
		this.decompressBytes = decompressBytes;
		this.compressByteBuffer = compressByteBuffer;
		this.decompressByteBuffer = decompressByteBuffer;
	}

	CompressionMethod(
		int id, String name,
		IOUnaryOperator<InputStream> input,
		IOUnaryOperator<OutputStream> output
	) {
		this(id, name, input, output, null, null, null, null);
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

	public byte[] compress(byte[] data) throws IOException {
		if (compressBytes != null) {
			return compressBytes.apply(data);
		}

		var byteOut = new ByteArrayOutputStream(Math.max(64, data.length / 4));

		try (var out = out(byteOut)) {
			out.write(data);
		}

		return byteOut.toByteArray();
	}

	public byte[] decompress(byte[] data) throws IOException {
		if (decompressBytes != null) {
			return decompressBytes.apply(data);
		}

		try (var in = in(new ByteArrayInputStream(data))) {
			return in.readAllBytes();
		}
	}

	public ByteBuffer compress(ByteBuffer data) throws IOException {
		if (compressByteBuffer != null) {
			return compressByteBuffer.apply(data);
		}

		if (data.hasArray() && data.position() == 0 && data.limit() == data.capacity()) {
			int limit = data.limit();
			var result = compress(data.array());
			data.limit(limit);
			return ByteBuffer.wrap(result);
		} else {
			var arr = new byte[data.remaining()];
			data.get(arr);
			return ByteBuffer.wrap(compress(arr));
		}
	}

	public ByteBuffer decompress(ByteBuffer data) throws IOException {
		if (decompressByteBuffer != null) {
			return decompressByteBuffer.apply(data);
		}

		if (data.hasArray() && data.position() == 0 && data.limit() == data.capacity()) {
			int limit = data.limit();
			var result = decompress(data.array());
			data.limit(limit);
			return ByteBuffer.wrap(result);
		} else {
			var arr = new byte[data.remaining()];
			data.get(arr);
			return ByteBuffer.wrap(decompress(arr));
		}
	}
}
