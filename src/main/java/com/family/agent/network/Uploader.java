package com.family.agent.network;

import java.net.Socket;

public class Uploader extends Thread {
	
	public Uploader()
	{
		
	}
	
	public void run()
	{
		try
		{
			Socket soc = new Socket("localhost", 5000);
			// gui hinh anh
			Screenshot s = new Screenshot(soc, 3000);
			s.start();

			Socket appSocket = new Socket("localhost", 5000);
			AppUsage appusage = new AppUsage(soc);
			appusage.start();
		}
		catch(Exception e)
		{
			
		}
	
		
	}
}


