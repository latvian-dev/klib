package dev.latvian.mods.klib.io;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.LongConsumer;
import java.util.function.Predicate;

public interface IOUtils {
	UUID NIL_UUID = new UUID(0L, 0L);
	ReentrantLock FS_LOCK = new ReentrantLock();
	ReentrantLock ZIP_FS_LOCK = new ReentrantLock();
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

	static void runUnmodified(Path path, PathOperation operation) throws IOException {
		var time = Files.getLastModifiedTime(path);

		try {
			operation.run(path);
		} finally {
			Files.setLastModifiedTime(path, time);
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
		return ByteBuffer.allocateDirect((int) Math.min(maxBufferSize, fileSize));
	}

	static ByteBuffer allocateTempBuffer(Path file) throws IOException {
		return allocateTempBuffer(16384, Files.size(file));
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
			var buf = allocateTempBuffer(16384, size);

			do {
				int len = (int) Math.min(size, buf.capacity());

				if (callback != null) {
					callback.accept(len);
				}

				buf.clear().limit(len);

				if (channel.read(buf) == -1) {
					break;
				}

				buf.flip();

				size -= len;
				md.update(buf);
			} while (size > 0L);

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

	static FileSystem openAsZip(Path path, Map<String, ?> env) throws IOException {
		return FileSystems.newFileSystem(URI.create("jar:" + path.toUri()), env);
	}

	static FileSystem openAsZip(Path path) throws IOException {
		return openAsZip(path, Map.of());
	}

	static List<String> platformCopy(String from, String to) {
		if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")) {
			// /E = Recursive + Empty subdirectories
			// /COPY:DAT = Copies Data, Attributes and Timestamps
			// /DCOPY:T = Copies directory Timestamps
			// /MT:16 = 16 threads
			return List.of("robocopy", from, to, "/E", "/COPY:DAT", "/DCOPY:T", "/MT:16");
		} else {
			// -R = Recursive
			// --preserve=all = Copy attributes
			return List.of("cp", "-R", "--preserve=all", from, to);
		}
	}

	static ByteBuffer toDirect(ByteBuffer buffer) {
		if (buffer.isDirect()) {
			return buffer;
		}

		var directBuffer = ByteBuffer.allocateDirect(buffer.remaining());
		directBuffer.put(buffer);
		return directBuffer;
	}

	static BufferedImage resize(BufferedImage image, int width, int height) {
		int w0 = image.getWidth();
		int h0 = image.getHeight();

		if (w0 == width && h0 == height) {
			return image;
		}

		double ratio;

		if (w0 > h0) {
			ratio = width / (float) w0;
		} else {
			ratio = height / (float) h0;
		}

		int w1 = Math.clamp(Mth.ceil(w0 * ratio), 1, width);
		int h1 = Math.clamp(Mth.ceil(h0 * ratio), 1, height);

		// App.info("Resizing image from " + w0 + "x" + h0 + " to " + w1 + "x" + h1 + " with ratio " + ratio + " and target size " + width + "x" + height);

		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resized.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(image, (width - w1) / 2, (height - h1) / 2, w1, h1, null);
		g.dispose();
		return resized;
	}
}
