package dev.latvian.mods.klib.io.checksum;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.io.FileInfo;
import dev.latvian.mods.klib.io.IOUtils;
import dev.latvian.mods.klib.io.bytes.ByteBufByteInput;
import dev.latvian.mods.klib.io.bytes.ByteBufByteOutput;
import dev.latvian.mods.klib.io.bytes.ByteInput;
import dev.latvian.mods.klib.util.StringUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Function;
import java.util.function.LongConsumer;

public class ChecksumType<C extends Checksum> {
	public static final List<ChecksumType<?>> TYPES = List.of(
		NoChecksum.TYPE,
		CRC32.TYPE,
		MD5.TYPE,
		SHA1.TYPE,
		SHA256.TYPE,
		SHA384.TYPE,
		SHA512.TYPE
	);

	public final int id;
	public final String name;
	public final String algorithm;
	public final C nil;
	public final int size;
	public final Function<byte[], C> fromBytes;
	public final Codec<C> codec;
	public final StreamCodec<ByteBuf, C> streamCodec;

	public static ChecksumType<?> typeOf(int id) {
		for (var type : TYPES) {
			if (type.id == id) {
				return type;
			}
		}

		throw new IllegalArgumentException("Unknown type: " + id);
	}

	public ChecksumType(
		int id,
		String name,
		String algorithm,
		C nil,
		int size,
		Function<byte[], C> fromBytes
	) {
		this.id = id;
		this.name = name;
		this.algorithm = algorithm;
		this.nil = nil;
		this.size = size;
		this.fromBytes = fromBytes;

		this.codec = Codec.STRING.comapFlatMap(string -> {
			if (string.isEmpty()) {
				return DataResult.success(this.nil);
			} else if (string.length() == this.size * 2 && StringUtils.isHex(string)) {
				return DataResult.success(of(StringUtils.fromHex(string)));
			} else {
				return DataResult.error(() -> "Invalid " + this.algorithm + " " + string);
			}
		}, checksum -> checksum.isNil() ? "" : checksum.toString());

		this.streamCodec = new StreamCodec<>() {
			@Override
			public C decode(ByteBuf buf) {
				try {
					return read(ByteBufByteInput.of(buf));
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}

			@Override
			public void encode(ByteBuf buf, C value) {
				try {
					value.write(ByteBufByteOutput.of(buf));
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		};
	}

	public C of(byte[] bytes) {
		return fromBytes.apply(bytes);
	}

	public C of(String string) {
		if (string.isEmpty()) {
			return nil;
		} else if (string.length() == size * 2 && StringUtils.isHex(string)) {
			return of(StringUtils.fromHex(string));
		} else {
			throw new IllegalArgumentException("Invalid " + algorithm + " " + string);
		}
	}

	public final C digest(Path file, @Nullable LongConsumer callback) throws IOException {
		if (Files.exists(file)) {
			long size = Files.size(file);

			if (size > 0L) {
				return digest(file, 0L, size, callback);
			}
		}

		return nil;
	}

	public final C digest(FileInfo fileInfo, @Nullable LongConsumer callback) throws IOException {
		return digest(fileInfo.path(), 0L, fileInfo.size(), callback);
	}

	public C digest(Path file, long offset, long size, @Nullable LongConsumer callback) throws IOException {
		if (size > 0L && Files.exists(file)) {
			try {
				var md = MessageDigest.getInstance(algorithm);
				IOUtils.readFile(file, offset, size, callback, md::update);
				return of(md.digest());
			} catch (NoSuchAlgorithmException ex) {
				throw new IOException(ex);
			}
		}

		return nil;
	}

	public C digest(byte[] input, int offset, int len) {
		if (len > 0) {
			try {
				var md = MessageDigest.getInstance(algorithm);
				md.update(input, offset, len);
				return of(md.digest());
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}
		}

		return nil;
	}

	public final C digest(byte[] input) {
		return digest(input, 0, input.length);
	}

	public C read(ByteInput data) throws IOException {
		var bytes = new byte[size];
		data.readAll(bytes);
		return of(bytes);
	}

	@Override
	public String toString() {
		return "ChecksumType[" + name + "]";
	}
}