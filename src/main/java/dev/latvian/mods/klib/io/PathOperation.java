package dev.latvian.mods.klib.io;

import java.io.IOException;
import java.nio.file.Path;

@FunctionalInterface
public interface PathOperation {
	void run(Path path) throws IOException;
}
