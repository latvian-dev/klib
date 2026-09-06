package dev.latvian.mods.klib.io.checksum;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.io.FileInfo;
import dev.latvian.mods.klib.io.IOUtils;
import dev.latvian.mods.klib.io.bytes.ByteInput;
import dev.latvian.mods.klib.io.bytes.ByteOutput;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.function.LongConsumer;

public record FileChecksum(Checksum checksum, long size, Instant lastModified, boolean changed) {
	public static final Codec<FileChecksum> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Checksum.CODEC.fieldOf("checksum").forGetter(FileChecksum::checksum),
		Codec.LONG.fieldOf("size").forGetter(FileChecksum::size),
		KLibCodecs.INSTANT.fieldOf("last_modified").forGetter(FileChecksum::lastModified),
		MapCodec.unit(false).forGetter(FileChecksum::changed)
	).apply(instance, FileChecksum::new));

	public static FileChecksum read(ChecksumType<?> type, ByteInput data) throws IOException {
		var checksum = type.read(data);
		var size = data.readVarLong();
		var lastModified = data.readExactTime();
		return new FileChecksum(checksum, size, lastModified, false);
	}

	@Nullable
	public static FileChecksum loadExisting(ChecksumType<?> type, FileInfo fileInfo) {
		try {
			var attribute = IOUtils.getAttributeBytes(fileInfo.path(), "latviandev-file-" + type.name);

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
		var existing = loadExisting(type, fileInfo);
		var lastModified = IOUtils.getLastModifiedTime(fileInfo.path());

		if (existing == null || fileInfo.size() != existing.size || lastModified == null || lastModified.isAfter(existing.lastModified)) {
			var checksum = type.digest(fileInfo.path(), 0L, fileInfo.size(), progress);

			return new FileChecksum(
				checksum,
				fileInfo.size(),
				lastModified,
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
		IOUtils.setAttributeBytes(file, "latviandev-file-" + type.name, data.toByteArray());
	}

	@Nullable
	public static FileChecksum loadChanged(ChecksumType<?> type, FileInfo fileInfo, @Nullable LongConsumer progress) throws IOException {
		var meta = load(type, fileInfo, progress);

		if (meta.changed()) {
			save(type, fileInfo.path(), meta);
			Files.setLastModifiedTime(fileInfo.path(), FileTime.from(meta.lastModified()));
			return meta;
		}

		return null;
	}

	public void write(ByteOutput data) throws IOException {
		checksum.write(data);
		data.writeVarLong(size);
		data.writeExactTime(lastModified);
	}
}
