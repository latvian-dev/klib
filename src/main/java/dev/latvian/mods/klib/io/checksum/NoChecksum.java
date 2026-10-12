package dev.latvian.mods.klib.io.checksum;

import dev.latvian.mods.klib.io.IOUtils;
import dev.latvian.mods.klib.io.bytes.ByteOutput;

import java.io.IOException;

public enum NoChecksum implements Checksum {
	INSTANCE;

	@Override
	public ChecksumType<?> type() {
		return NoChecksumType.TYPE;
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
