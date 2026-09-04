package dev.latvian.mods.klib.io.checksum;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.io.FileInfo;
import dev.latvian.mods.klib.io.IOUtils;
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
		CRC32.TYPE,
		MD5.TYPE,
		SHA1.TYPE,
		SHA256.TYPE,
		SHA384.TYPE,
		SHA512.TYPE
	);

	public final int id;
	public final String name;
	public final String md;
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
		String md,
		C nil,
		int size,
		Function<byte[], C> fromBytes
	) {
		this.id = id;
		this.name = name;
		this.md = md;
		this.nil = nil;
		this.size = size;
		this.fromBytes = fromBytes;

		this.codec = Codec.STRING.comapFlatMap(string -> {
			if (string.isEmpty()) {
				return DataResult.success(this.nil);
			} else if (string.length() == this.size * 2 && StringUtils.isHex(string)) {
				return DataResult.success(of(StringUtils.fromHex(string)));
			} else {
				return DataResult.error(() -> "Invalid " + this.md + " " + string);
			}
		}, checksum -> checksum.isNil() ? "" : checksum.toString());

		this.streamCodec = new StreamCodec<>() {
			@Override
			public C decode(ByteBuf buf) {
				return ChecksumType.this.decode(buf);
			}

			@Override
			public void encode(ByteBuf buf, C value) {
				value.encode(buf);
			}
		};
	}

	public C of(byte[] bytes) {
		return fromBytes.apply(bytes);
	}

	public C decode(ByteBuf buf) {
		var bytes = new byte[size];
		buf.readBytes(bytes);
		return of(bytes);
	}

	public C of(String string) {
		if (string.isEmpty()) {
			return nil;
		} else if (string.length() == size * 2 && StringUtils.isHex(string)) {
			return of(StringUtils.fromHex(string));
		} else {
			throw new IllegalArgumentException("Invalid " + md + " " + string);
		}
	}

	public C digest(Path file, @Nullable LongConsumer callback) throws IOException {
		if (Files.exists(file)) {
			long size = Files.size(file);

			if (size > 0L) {
				return digest(new FileInfo(file, "file", size), callback);
			}
		}

		return nil;
	}

	public C digest(FileInfo fileInfo, @Nullable LongConsumer callback) throws IOException {
		if (fileInfo.size() > 0L && Files.exists(fileInfo.path())) {
			return of(IOUtils.digest(md, fileInfo.path(), fileInfo.size(), callback));
		}

		return nil;
	}

	public C digest(byte[] input) throws NoSuchAlgorithmException {
		return of(MessageDigest.getInstance(md).digest(input));
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