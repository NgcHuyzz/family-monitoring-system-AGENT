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
		}
		catch(Exception e)
		{
			
		}
	
		
	}
}


