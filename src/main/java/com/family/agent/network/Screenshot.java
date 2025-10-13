package com.family.agent.network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.Socket;
import java.util.UUID;

import com.family.agent.collector.ScreenshotTask;
import com.family.agent.model.ScreenshotModel;

public class Screenshot extends Thread {
	
	private final long timeSend;
	private Socket soc;
	private String deviceID;
	
	public Screenshot(Socket soc, long timeSend)
	{
		this.soc = soc;
		this.timeSend = timeSend;
		this.deviceID = getOrCreateDeviceID();
	}
	public void run()
	{
		try
		{
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(soc.getOutputStream()));
			
			// gui ten thiet bi agent di
			dos.writeUTF(deviceID);
			dos.flush();
			
			ScreenshotTask st = new ScreenshotTask(1200, 720, timeSend, 0.8f);
			st.start();
			
			long lastSeq = -1;
			
			while(true)
			{
				try
				{
					
					long t0 = System.nanoTime();
					ScreenshotModel sm = st.latestFrame;
					if(sm != null && sm.getSequenceNum() != lastSeq)
					{				
						dos.writeInt(sm.getSize());
						dos.write(sm.getData());
						dos.writeInt(sm.getWidth());
						dos.writeInt(sm.getHeight());
						dos.writeLong(sm.getTakeAt().getTime());
						dos.flush();
						
						lastSeq = sm.getSequenceNum();
					}
					long timeUsed = (System.nanoTime() - t0) / 1_000_000L;
					long sleep = timeSend - timeUsed;
					Thread.sleep(sleep);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					break;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				soc.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private String getOrCreateDeviceID()
	{
		try
		{
			File file = new File("deviceID.txt");
			if(file.exists())
			{
				BufferedReader br = new BufferedReader(new FileReader(file));
				String id = br.readLine().trim();
				br.close();
				return id;
			}
			else
			{
				String id = UUID.randomUUID().toString();
				FileWriter fw = new FileWriter(file);
				fw.write(id);
				fw.close();
				
				return id;
			}
		}
		catch(Exception e)
		{
			
		}
		
		return "unknown";
	}
}
