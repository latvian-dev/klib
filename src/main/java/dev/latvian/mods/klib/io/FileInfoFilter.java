package dev.latvian.mods.klib.io;

@FunctionalInterface
public interface FileInfoFilter {
	boolean test(FileInfo fileInfo) throws Exception;
}
