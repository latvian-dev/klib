package dev.latvian.mods.klib.io;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.util.MD5;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
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

	public static FileMD5 read(DataInput data) throws IOException {
		var checksum = MD5.read(data);
		var size = IOUtils.readVarLong(data);
		var lastModified = IOUtils.readExactTime(data);
		return new FileMD5(checksum, size, lastModified, false);
	}

	@Nullable
	public static FileMD5 loadExisting(FileInfo fileInfo) {
		try {
			var attribute = IOUtils.getAttributeBytes(fileInfo.path(), "latviandev-file-md5");

			if (attribute != null) {
				try (var data = new DataInputStream(new ByteArrayInputStream(attribute))) {
					data.readUnsignedByte(); // Binary marker
					return read(data);
				}
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
		try (var bytes = new ByteArrayOutputStream();
			 var data = new DataOutputStream(bytes)
		) {
			data.writeByte(0);
			metadata.write(data);
			IOUtils.setAttributeBytes(file, "latviandev-file-md5", bytes.toByteArray());
		}
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

	public void write(DataOutput data) throws IOException {
		checksum.write(data);
		IOUtils.writeVarLong(data, size);
		IOUtils.writeExactTime(data, lastModified);
	}
}
