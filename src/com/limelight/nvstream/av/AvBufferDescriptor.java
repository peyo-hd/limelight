package com.limelight.nvstream.av;

public class AvBufferDescriptor {
	public byte[] data;
	public int offset;
	public int length;
	
	public AvBufferDescriptor(byte[] data, int offset, int length)
	{
		this.data = data;
		this.offset = offset;
		this.length = length;
	}
	
	public AvBufferDescriptor(AvBufferDescriptor desc)
	{
		this.data = desc.data;
		this.offset = desc.offset;
		this.length = desc.length;
	}
	
	public void print()
	{
		print(offset, length);
	}
	
	public void print(int length)
	{
		print(this.offset, length);
	}
	
	public void print(int offset, int length)
	{
		for (int i = offset; i < offset+length; i++) {
			System.out.printf("%d: %02x \n", i, data[i]);
		}
		System.out.println();
	}
}
