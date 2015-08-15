package yuku.bintex;

import java.io.IOException;
import java.io.OutputStream;

public class BintexWriter {
	private final OutputStream os_;

	/** 
	 * Tambah hanya kalau manggil os_.write(*) Jangan tambah kalo ga.
	 */
	private int pos = 0;

	public BintexWriter(OutputStream os) {
		this.os_ = os;
	}
	
	public void writeShortString(String s) throws IOException {
		int len = s.length();
		
		if (len > 255) {
			throw new IllegalArgumentException("string must not more than 255 chars. String is: " + s); //$NON-NLS-1$
		}
		
		os_.write(len);
		pos += 1;
		
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			
			writeChar(c);
		}
	}
	
	public void writeLongString(String s) throws IOException {
		writeInt(s.length());
		
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			
			writeChar(c);
		}
	}
	
	/**
	 * Tulis pake 8-bit atau 16-bit
	 * 
	 * byte pertama menentukan
	 * 0x01 = 8 bit short
	 * 0x02 = 16 bit short
	 * 0x11 = 8 bit long
	 * 0x12 = 16 bit long
	 */
	public void writeAutoString(String s) throws IOException {
		// cek dulu apa semuanya 8 bit
		boolean semua8bit = true;
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c > 0xff) {
				semua8bit = false;
				break;
			}
		}
		
		if (len <= 255 && semua8bit) writeUint8(0x01);
		if (len >  255 && semua8bit) writeUint8(0x11);
		if (len <= 255 && !semua8bit) writeUint8(0x02);
		if (len >  255 && !semua8bit) writeUint8(0x12);
		
		if (len <= 255) {
			writeUint8(len);
		} else {
			writeInt(len);
		}
		
		if (semua8bit) {
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				writeUint8(c);
			}
		} else {
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				writeChar(c);
			}
		}
	}
	
	public void writeInt(int a) throws IOException {
		os_.write((a & 0xff000000) >> 24);
		os_.write((a & 0x00ff0000) >> 16);
		os_.write((a & 0x0000ff00) >> 8);
		os_.write((a & 0x000000ff) >> 0);
		
		pos += 4;
	}
	
	public void writeChar(char c) throws IOException {
		os_.write((c & 0xff00) >> 8);
		os_.write(c & 0x00ff);
		
		pos += 2;
	}
	
	public void writeUint8(int a) throws IOException {
		if (a < 0 || a > 255) {
			throw new IllegalArgumentException("uint8 must be 0 to 255"); //$NON-NLS-1$
		}
		
		os_.write(a);
		
		pos += 1;
	}
	
	public void writeUint16(int a) throws IOException {
		if (a < 0 || a > 0xffff) {
			throw new IllegalArgumentException("uint16 must be 0 to 65535"); //$NON-NLS-1$
		}
		
		os_.write((a & 0x0000ff00) >> 8);
		os_.write((a & 0x000000ff) >> 0);
		
		pos += 2;
	}
	
	public void writeFloat(float f) throws IOException {
		int a = Float.floatToIntBits(f);
		writeInt(a);
	}
	
	public void writeRaw(byte[] buf) throws IOException {
		writeRaw(buf, 0, buf.length);
	}
	
	public void writeRaw(byte[] buf, int off, int len) throws IOException {
		os_.write(buf, off, len);
		
		pos += len;
	}
	
	public void close() throws IOException {
		os_.close();
	}
	
	public int getPos() {
		return pos;
	}

	public OutputStream getOutputStream() {
		return new OutputStream() {
			@Override
			public void write(int oneByte) throws IOException {
				writeUint8(oneByte);
			}
			
			@Override
			public void write(byte[] buffer, int offset, int count) throws IOException {
				writeRaw(buffer, offset, count);
			}
		};
	}
}
