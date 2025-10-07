package com.family.agent.model;

import java.sql.Timestamp;

public class ScreenshotModel {
	private int size;
	private byte[] data;
	private int width;
	private int height;
	private long sequenceNum;
	private Timestamp takeAt;
	
	public ScreenshotModel()
	{
		
	}
	
	public ScreenshotModel(int size, byte[] data, int width, int height, long sequenceNum, Timestamp takeAt)
	{
		this.size = size;
		this.data = data;
		this.width = width;
		this.height = height;
		this.sequenceNum = sequenceNum;
		this.takeAt = takeAt;
	}
	
	public int getSize()
	{
		return this.size;
	}
	
	public byte[] getData()
	{
		return this.data;
	}
	
	public int getWidth()
	{
		return this.width;
	}
	
	public int getHeight()
	{
		return this.height;
	}
	
	public long getSequenceNum()
	{
		return this.sequenceNum;
	}
	
	public Timestamp getTakeAt()
	{
		return this.takeAt;
	}
	
	public void setSize(int size)
	{
		this.size = size;
	}
	
	public void setData(byte[] data)
	{
		this.data = data;
	}
	
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	public void setSequenceNum(long sequenceNum)
	{
		this.sequenceNum = sequenceNum;
	}
	
	public void setTakeAt(Timestamp takeAt)
	{
		this.takeAt = takeAt;
	}
	
}
