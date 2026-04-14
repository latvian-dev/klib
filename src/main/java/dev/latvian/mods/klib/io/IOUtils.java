package dev.latvian.mods.klib.io;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.LongConsumer;
import java.util.function.Predicate;

public interface IOUtils {
	Set<StandardOpenOption> WRITE_OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	Set<StandardOpenOption> APPEND_OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

	static long getSize(Path path) {
		try {
			return Files.size(path);
		} catch (Exception ex) {
			return -1L;
		}
	}

	@Nullable
	static Instant getCreatedTime(Path path) {
		try {
			var view = Files.getFileAttributeView(path, BasicFileAttributeView.class);
			var instant = view.readAttributes().creationTime().toInstant();
			return instant.toEpochMilli() == 0L ? null : instant;
		} catch (Exception ex) {
			return null;
		}
	}

	@Nullable
	static Instant getLastModifiedTime(Path path) {
		try {
			var view = Files.getFileAttributeView(path, BasicFileAttributeView.class);
			var instant = view.readAttributes().lastModifiedTime().toInstant();
			return instant.toEpochMilli() == 0L ? null : instant;
		} catch (Exception ex) {
			return null;
		}
	}

	@Nullable
	static Instant getLastAccessedTime(Path path) {
		try {
			var view = Files.getFileAttributeView(path, BasicFileAttributeView.class);
			var instant = view.readAttributes().lastAccessTime().toInstant();
			return instant.toEpochMilli() == 0L ? null : instant;
		} catch (Exception ex) {
			return null;
		}
	}

	static void deleteRecursively(Path dir) throws IOException {
		try (var stream = Files.walk(dir)) {
			stream.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(File::delete);
		}
	}

	static void clearDirectory(Path dir) throws IOException {
		try (var stream = Files.list(dir)) {
			for (var file : stream.toList()) {
				deleteRecursively(dir);
			}
		}
	}

	static int readVarInt(DataInput in) throws IOException {
		int value = 0;
		int count = 0;

		byte b0;
		do {
			b0 = in.readByte();
			value |= (b0 & 127) << count++ * 7;

			if (count > 5) {
				throw new RuntimeException("VarInt too big");
			}
		} while ((b0 & 128) == 128);

		return value;
	}

	static long readVarLong(DataInput in) throws IOException {
		long value = 0L;
		int count = 0;

		byte b0;
		do {
			b0 = in.readByte();
			value |= (long) (b0 & 127) << count++ * 7;

			if (count > 10) {
				throw new RuntimeException("VarLong too big");
			}
		} while ((b0 & 128) == 128);

		return value;
	}

	static byte[] readBytes(DataInput in) throws IOException {
		int length = readVarInt(in);
		var bytes = new byte[length];
		in.readFully(bytes);
		return bytes;
	}

	static String readUTF(DataInput in) throws IOException {
		return new String(readBytes(in), StandardCharsets.UTF_8);
	}

	static Instant readExactTime(DataInput in) throws IOException {
		var second = readVarLong(in);
		var nano = readVarInt(in);
		return Instant.ofEpochSecond(second, nano);
	}

	static void writeVarInt(DataOutput out, int value) throws IOException {
		while ((value & -128) != 0) {
			out.writeByte(value & 127 | 128);
			value >>>= 7;
		}

		out.writeByte(value);
	}

	static void writeVarLong(DataOutput out, long value) throws IOException {
		while ((value & -128L) != 0L) {
			out.writeByte((int) (value & 127L) | 128);
			value >>>= 7;
		}

		out.writeByte((int) value);
	}

	static void writeBytes(DataOutput out, byte[] bytes) throws IOException {
		writeVarInt(out, bytes.length);
		out.write(bytes);
	}

	static void writeUTF(DataOutput out, String value) throws IOException {
		writeBytes(out, value.getBytes(StandardCharsets.UTF_8));
	}

	static void writeExactTime(DataOutput out, Instant value) throws IOException {
		writeVarLong(out, value.getEpochSecond());
		writeVarInt(out, value.getNano());
	}

	static byte[] toByteArray(ByteBuf buf, boolean release) {
		var bytes = new byte[buf.readableBytes()];
		buf.getBytes(buf.readerIndex(), bytes);

		if (release) {
			buf.release();
		}

		return bytes;
	}

	static void writeBytes(Path path, ByteBuffer buf, long remainingBytes) throws IOException {
		if (remainingBytes <= 0L) {
			return;
		}

		try (var channel = Files.newByteChannel(path, WRITE_OPEN_OPTIONS)) {
			long writtenBytes;

			while ((writtenBytes = channel.write(buf)) < remainingBytes) {
				remainingBytes -= writtenBytes;

				if (remainingBytes <= 0) {
					break;
				}
			}
		}
	}

	static void appendBytes(Path path, ByteBuffer buf, long remainingBytes) throws IOException {
		if (remainingBytes <= 0L) {
			return;
		}

		try (var channel = Files.newByteChannel(path, APPEND_OPEN_OPTIONS)) {
			long writtenBytes;

			while ((writtenBytes = channel.write(buf)) < remainingBytes) {
				remainingBytes -= writtenBytes;

				if (remainingBytes <= 0) {
					break;
				}
			}
		}
	}

	static ByteBuffer allocateTempBuffer(int maxBufferSize, long fileSize) {
		return ByteBuffer.allocate(Math.min(maxBufferSize, (int) Math.min(Integer.MAX_VALUE, fileSize)));
	}

	static ByteBuffer allocateTempBuffer(Path file) throws IOException {
		return allocateTempBuffer(4096, Files.size(file));
	}

	static MessageDigest md(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
	}

	static MessageDigest md5() {
		return md("MD5");
	}

	static byte[] digest(String algorithm, Path file, long size, @Nullable LongConsumer callback) throws IOException {
		var md = md(algorithm);

		try (var channel = Files.newByteChannel(file)) {
			var buf = allocateTempBuffer(4096, size);
			int len;

			while ((len = channel.read(buf)) != -1) {
				buf.flip();
				md.update(buf);
				buf.clear();

				if (callback != null) {
					callback.accept(len);
				}
			}

			return md.digest();
		}
	}

	@Nullable
	static ByteBuffer getAttributeBuffer(Path file, String attribute) throws IOException {
		var attributes = Files.getFileAttributeView(file, UserDefinedFileAttributeView.class);

		if (attributes != null && attributes.list().contains(attribute)) {
			var attributeBuffer = ByteBuffer.allocate(attributes.size(attribute));
			attributes.read(attribute, attributeBuffer);
			attributeBuffer.flip();
			return attributeBuffer;
		}

		return null;
	}

	static String getAttribute(Path file, String attribute) throws IOException {
		var buffer = getAttributeBuffer(file, attribute);
		return buffer == null ? "" : Charset.defaultCharset().decode(buffer).toString();
	}

	@Nullable
	static byte[] getAttributeBytes(Path file, String attribute) throws IOException {
		var buffer = getAttributeBuffer(file, attribute);
		return buffer == null ? null : buffer.array();
	}

	static boolean setAttributeBuffer(Path file, String attribute, ByteBuffer value) throws IOException {
		var attributes = Files.getFileAttributeView(file, UserDefinedFileAttributeView.class);

		if (attributes != null) {
			return attributes.write(attribute, value) > 0;
		}

		return false;
	}

	static boolean setAttribute(Path file, String attribute, String value) throws IOException {
		return setAttributeBuffer(file, attribute, Charset.defaultCharset().encode(value));
	}

	static boolean setAttributeBytes(Path file, String attribute, byte[] value) throws IOException {
		return setAttributeBuffer(file, attribute, ByteBuffer.wrap(value));
	}

	static Predicate<Path> pathEndsWith(String suffix) {
		return path -> path.toString().endsWith(suffix);
	}
}
