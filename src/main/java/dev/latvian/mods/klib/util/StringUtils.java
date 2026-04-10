package dev.latvian.mods.klib.util;

import dev.latvian.mods.klib.math.KMath;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TimeZone;
import java.util.UUID;

public interface StringUtils {
	Set<String> ALWAYS_LOWER_CASE = new HashSet<>(Arrays.asList("a", "an", "the", "of", "on", "in", "and", "or", "but", "for"));
	byte[] HEX_ARRAY = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);
	Base64.Encoder B64_ENCODER = Base64.getUrlEncoder().withoutPadding();
	Base64.Decoder B64_DECODER = Base64.getUrlDecoder();

	SimpleDateFormat SHORT_EST_TIMESTAMP_FORMAT = Util.make(() -> {
		var format = new SimpleDateFormat("HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return format;
	});

	SimpleDateFormat LONG_EST_TIMESTAMP_FORMAT = Util.make(() -> {
		var format = new SimpleDateFormat("EEEE, d MMM yyyy, HH:mm:ss.SSS");
		format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return format;
	});

	SimpleDateFormat LONG_LOCAL_TIMESTAMP_FORMAT = new SimpleDateFormat("EEEE, d MMM yyyy, HH:mm:ss.SSS");

	DecimalFormat BYTE_SIZE_FORMAT = new DecimalFormat("#,##0.#");
	String[] BINARY_BYTE_SIZE_UNITS = new String[]{"B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB"};
	String[] SI_BYTE_SIZE_UNITS = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB"};

	static String snakeCaseToTitleCase(String string) {
		StringJoiner joiner = new StringJoiner(" ");
		String[] split = string.split("_");
		for (int i = 0; i < split.length; i++) {
			String s = split[i];
			String titleCase = toTitleCase(s, i == 0);
			joiner.add(titleCase);
		}
		return joiner.toString();
	}

	static String toTitleCase(String s) {
		return toTitleCase(s, false);
	}

	static String toTitleCase(String s, boolean ignoreSpecial) {
		if (s.isEmpty()) {
			return "";
		} else if (!ignoreSpecial && ALWAYS_LOWER_CASE.contains(s)) {
			return s;
		} else if (s.length() == 1) {
			return s.toUpperCase(Locale.ROOT);
		}

		char[] chars = s.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

	static String timer(long millis) {
		return "%02d:%02d:%03d".formatted(millis / 60000L, (millis / 1000L) % 60, millis % 1000L);
	}

	static String clock(long seconds) {
		return "%02d:%02d:%02d ".formatted(seconds / 3600L, (seconds / 60L) % 60L, seconds % 60L);
	}

	@Nullable
	static Component buildComponent(@Nullable Component prefix, @Nullable Component original, @Nullable Component suffix) {
		if (Empty.isEmpty(prefix)) {
			prefix = null;
		}

		if (Empty.isEmpty(suffix)) {
			suffix = null;
		}

		if (prefix == null && suffix == null) {
			return original;
		}

		if (Empty.isEmpty(original)) {
			original = null;
		}

		var component = Component.empty();

		if (prefix != null) {
			component.append(prefix);
		}

		if (original != null) {
			component.append(original);
		}

		if (suffix != null) {
			component.append(suffix);
		}

		return component;
	}

	static String normalizeFileName(String name) {
		name = name.trim();
		int index = name.lastIndexOf('.');

		if (index != -1) {
			name = name.substring(0, index);
		}

		name = name.replaceAll("[^-\\w]", "_").replaceAll("_{2,}", "_");

		if (name.startsWith("_")) {
			name = name.substring(1);
		}

		if (name.endsWith("_")) {
			name = name.substring(0, name.length() - 1);
		}

		return name.isBlank() ? "" : name;
	}

	static UUID uuidFromString(String value) {
		return UUID.fromString(value.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
	}

	static String uuidToString(UUID value) {
		return value.toString().replace("-", "");
	}

	static String toHex(byte[] array) {
		var chars = new byte[array.length * 2];

		for (int i = 0; i < array.length; i++) {
			int v = array[i] & 0xFF;
			chars[i * 2] = HEX_ARRAY[v >>> 4];
			chars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}

		return new String(chars, StandardCharsets.UTF_8);
	}

	static byte[] fromHex(String string) {
		int len = string.length();
		byte[] bytes = new byte[len / 2];

		for (int i = 0; i < len; i += 2) {
			int m = Character.digit(string.charAt(i), 16) << 4;
			int l = Character.digit(string.charAt(i + 1), 16);
			bytes[i / 2] = (byte) (m + l);
		}

		return bytes;
	}

	static String binaryByteSize(long size) {
		if (size <= 0L) {
			return "0 B";
		} else if (size < 1024L) {
			return size + " B";
		}

		int digitGroups = (int) (Math.log10(size) / KMath.LOG_10_OF_1024);
		return BYTE_SIZE_FORMAT.format(size / Math.pow(1024D, digitGroups)) + " " + BINARY_BYTE_SIZE_UNITS[digitGroups];
	}

	static String siByteSize(long size) {
		if (size <= 0L) {
			return "0 B";
		} else if (size < 1000L) {
			return size + " B";
		}

		int digitGroups = (int) (Math.log10(size) / KMath.LOG_10_OF_1000);
		return BYTE_SIZE_FORMAT.format(size / Math.pow(1000D, digitGroups)) + " " + SI_BYTE_SIZE_UNITS[digitGroups];
	}
}
