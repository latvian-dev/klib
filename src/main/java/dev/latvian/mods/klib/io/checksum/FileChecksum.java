package dev.latvian.mods.klib.io.checksum;

import dev.latvian.mods.klib.io.FileInfo;
import dev.latvian.mods.klib.io.IOUtils;
import dev.latvian.mods.klib.io.bytes.ByteInput;
import dev.latvian.mods.klib.io.bytes.ByteOutput;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.util.function.LongConsumer;

public record FileChecksum(Checksum checksum, long size, Instant lastModified, Instant lastWritten, boolean changed) {
	public static final Duration REFRESH_TIME = Duration.ofDays(3L);

	public static FileChecksum read(ChecksumType<?> type, ByteInput data) throws IOException {
		var checksum = type.read(data);
		var size = data.readVarLong();
		var lastModified = data.readExactTime();
		var lastWritten = data.readExactTime();
		return new FileChecksum(checksum, size, lastModified, lastWritten, false);
	}

	@Nullable
	public static FileChecksum loadExisting(ChecksumType<?> type, Path path) {
		try {
			var attribute = IOUtils.getAttributeBytes(path, type.attribute);

			if (attribute != null) {
				var data = ByteInput.of(attribute);
				data.readUByte(); // Binary marker
				return read(type, data);
			}
		} catch (Exception ignored) {
		}

		return null;
	}

	public static FileChecksum load(ChecksumType<?> type, FileInfo fileInfo, @Nullable LongConsumer progress) throws IOException {
		var now = Instant.now();
		var existing = loadExisting(type, fileInfo.path());
		var lastModified = IOUtils.getLastModifiedTime(fileInfo.path());

		if (existing == null || fileInfo.size() != existing.size || lastModified == null || lastModified.isAfter(existing.lastModified) || Duration.between(existing.lastWritten, now).compareTo(REFRESH_TIME) > 0) {
			var checksum = type.digest(fileInfo.path(), 0L, fileInfo.size(), progress);

			return new FileChecksum(
				checksum,
				fileInfo.size(),
				lastModified,
				now,
				true
			);
		} else if (progress != null) {
			progress.accept(fileInfo.size());
		}

		return existing;
	}

	public static void save(ChecksumType<?> type, Path file, FileChecksum metadata) throws IOException {
		var data = ByteOutput.ofByteBuilder();
		data.writeUByte(0);
		metadata.write(data);
		IOUtils.setAttributeBytes(file, type.attribute, data.toByteArray());
	}

	public static FileChecksum loadAndSave(ChecksumType<?> type, Path path, @Nullable LongConsumer progress) throws IOException {
		var attributes = Files.readAttributes(path, BasicFileAttributes.class);

		var meta = load(type, new FileInfo(path, "", attributes.size()), progress);

		if (meta.changed()) {
			save(type, path, meta);
			Files.setLastModifiedTime(path, attributes.lastModifiedTime());
		}

		return meta;
	}

	public void write(ByteOutput data) throws IOException {
		checksum.write(data);
		data.writeVarLong(size);
		data.writeExactTime(lastModified);
		data.writeExactTime(lastWritten);
	}
}
