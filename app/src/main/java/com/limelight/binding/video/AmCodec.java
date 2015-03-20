package com.limelight.binding.video;

import java.nio.ByteBuffer;

public class AmCodec {
	static {
		System.loadLibrary("jni_amcodec");
	}

	private long mNativeContext;

	public native final int init(int width, int height);
	public native final int write(ByteBuffer buffer, int offset, int size);
	public native final int buflen();
	public native final int close();

}
