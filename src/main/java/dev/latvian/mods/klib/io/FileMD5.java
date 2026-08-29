package dev.latvian.mods.klib.io;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.util.MD5;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.function.LongConsumer;

public record FileMD5(MD5 checksum, long size, Instant lastModified, boolean changed) {
	public static final Codec<FileMD5> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		MD5.CODEC.fieldOf("md5").forGetter(FileMD5::checksum),
		Codec.LONG.fieldOf("size").forGetter(FileMD5::size),
		KLibCodecs.INSTANT.fieldOf("last_modified").forGetter(FileMD5::lastModified),
		MapCodec.unit(false).forGetter(FileMD5::changed)
	).apply(instance, FileMD5::new));

	public static FileMD5 read(ByteInput data) throws IOException {
		var checksum = MD5.read(data);
		var size = data.readVarLong();
		var lastModified = data.readExactTime();
		return new FileMD5(checksum, size, lastModified, false);
	}

	@Nullable
	public static FileMD5 loadExisting(FileInfo fileInfo) {
		try {
			var attribute = IOUtils.getAttributeBytes(fileInfo.path(), "latviandev-file-md5");

			if (attribute != null) {
				var data = ByteInput.of(attribute);
				data.readUByte(); // Binary marker
				return read(data);
			}
		} catch (Exception ignored) {
		}

		return null;
	}

	public static FileMD5 load(FileInfo fileInfo, @Nullable LongConsumer progress) throws IOException {
		var existing = loadExisting(fileInfo);
		var lastModified = IOUtils.getLastModifiedTime(fileInfo.path());

		if (existing == null || fileInfo.size() != existing.size || lastModified == null || lastModified.isAfter(existing.lastModified)) {
			var md5 = MD5.of(fileInfo, progress);

			return new FileMD5(
				md5,
				fileInfo.size(),
				lastModified,
				true
			);
		} else if (progress != null) {
			progress.accept(fileInfo.size());
		}

		return existing;
	}

	public static void save(Path file, FileMD5 metadata) throws IOException {
		var data = ByteOutput.ofByteBuilder(16);
		data.writeUByte(0);
		metadata.write(data);
		IOUtils.setAttributeBytes(file, "latviandev-file-md5", data.toByteArray());
	}

	@Nullable
	public static FileMD5 loadChanged(FileInfo fileInfo, @Nullable LongConsumer progress) throws IOException {
		var meta = load(fileInfo, progress);

		if (meta.changed()) {
			save(fileInfo.path(), meta);
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
