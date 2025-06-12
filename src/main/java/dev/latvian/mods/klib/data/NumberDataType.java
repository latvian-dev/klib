package dev.latvian.mods.klib.data;

import org.jetbrains.annotations.Nullable;

public interface NumberDataType {
	@Nullable
	Number toNumber(DataType<?> type);
}
