package dev.latvian.mods.klib.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class ByteArrayDataOutputStream extends DataOutputStream {
	public ByteArrayDataOutputStream(ByteArrayOutputStream out) {
		super(out);
	}

	public byte[] toByteArray() {
		return ((ByteArrayOutputStream) out).toByteArray();
	}
}
