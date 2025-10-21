package com.family.agent.collector;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import com.family.agent.model.ScreenshotModel;

public class ScreenshotTask extends Thread {
	public volatile ScreenshotModel latestFrame;
	
	private final int width;
	private final int height;
	private final long timeSend;
	private final float jpgQuality;
	
	private static long seq = 0;
	private static synchronized long nextSeq()
	{
		return ++seq;
	}
	
	public ScreenshotTask() 
	{
		this(1200, 720, 3000L, 0.60f);
	}
	
	public ScreenshotTask(int width, int height, long timeSend, float jpgQuality)
	{
		this.width = width;
		this.height = height;
		this.timeSend = timeSend;
		this.jpgQuality = jpgQuality;
		
		// dat ten thread screenshotTask
		setName("ScreenshotTask");
		// Khi main dung thi cai nay tu dong dung
		setDaemon(true);
	}
	
	
	public void run()
	{
		Robot r = null;
		Rectangle capture = null;
		
		BufferedImage img = null;
		
		ImageWriter writer = null;
		ImageWriteParam iwp = null;
		
		ByteArrayOutputStream baos = null;
		MemoryCacheImageOutputStream mcios = null;
		
		try
		{
			r = new Robot();
			capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			
			// lay danh sach imagewriter dung duoc voi jpg
			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
			writer = writers.next();
			iwp = writer.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // set mode 
			iwp.setCompressionQuality(jpgQuality); // ap muc chat luong
			
			// hung du lieu hinh anh
			baos = new ByteArrayOutputStream(256*1024);
			mcios = new MemoryCacheImageOutputStream(baos);
			writer.setOutput(mcios);
		}
		catch(Exception e)
		{
			
		}
		
		while(true)
		{
			try
			{
				long t0 = System.nanoTime();
				// chup man hinh
				BufferedImage src = r.createScreenCapture(capture);
				
				// luu src vao img
				Graphics2D g = img.createGraphics();
				g.drawImage(src, 0, 0, width, height, null);
				g.dispose();
				
				// nen vao baos
				baos.reset();
				writer.write(null, new IIOImage(img, null, null), iwp);
				mcios.flush();
				
				// luu vao dataImage
				byte[] dataImage = baos.toByteArray();
				
				// luu vao ScreenshotModel
				ScreenshotModel sm = new ScreenshotModel(dataImage.length, dataImage, width, height, nextSeq(), new java.sql.Timestamp(System.currentTimeMillis()));
				latestFrame = sm;
				
				// gioi han thoi luong chup anh la 3s
				long timeUsed = (System.nanoTime() - t0) / 1_000_000L;
				long sleep = timeSend - timeUsed;
				Thread.sleep(sleep);
			}
			catch(Exception e)
			{
				
			}		
		}
	}
	
}