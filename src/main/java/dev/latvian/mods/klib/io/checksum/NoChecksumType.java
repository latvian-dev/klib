package dev.latvian.mods.klib.io.checksum;

import dev.latvian.mods.klib.io.bytes.ByteInput;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.LongConsumer;

public class NoChecksumType extends ChecksumType<NoChecksum> {
	public static final NoChecksumType TYPE = new NoChecksumType();

	public NoChecksumType() {
		super(0, "none", "None", NoChecksum.INSTANCE, 0, null);
	}

	@Override
	public NoChecksum of(byte[] bytes) {
		return nil;
	}

	@Override
	public NoChecksum read(ByteInput data) throws IOException {
		return nil;
	}

	@Override
	public NoChecksum digest(Path file, long offset, long size, @Nullable LongConsumer callback) {
		return nil;
	}

	@Override
	public NoChecksum digest(byte[] input, int offset, int len) {
		return nil;
	}
}
