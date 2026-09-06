package dev.latvian.mods.klib.io.checksum;

import dev.latvian.mods.klib.io.IOUtils;
import dev.latvian.mods.klib.io.bytes.ByteInput;
import dev.latvian.mods.klib.io.bytes.ByteOutput;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.LongConsumer;

public enum NoChecksum implements Checksum {
	INSTANCE;

	public static final ChecksumType<NoChecksum> TYPE = new ChecksumType<>(0, "none", "None", INSTANCE, 0, b -> INSTANCE) {
		@Override
		public NoChecksum read(ByteInput data) {
			return INSTANCE;
		}

		@Override
		public NoChecksum digest(Path file, long offset, long size, @Nullable LongConsumer callback) {
			return INSTANCE;
		}

		@Override
		public NoChecksum digest(byte[] input, int offset, int len) {
			return INSTANCE;
		}
	};

	@Override
	public ChecksumType<?> type() {
		return TYPE;
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public boolean isNil() {
		return true;
	}

	@Override
	public byte[] toByteArray() {
		return IOUtils.EMPTY_BYTE_ARRAY;
	}

	@Override
	public void write(ByteOutput data) throws IOException {
	}
}
