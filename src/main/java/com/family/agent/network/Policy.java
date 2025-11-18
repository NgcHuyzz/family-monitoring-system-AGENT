package com.family.agent.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Policy extends Thread {
	private Socket soc;

	private String deviceID;
	public Policy(Socket soc)
	{
		this.soc = soc;
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
			
			DataInputStream dis = new DataInputStream(new BufferedInputStream(soc.getInputStream()));

			int dailyTimeQuote = dis.readInt();
			String quietHourJson = dis.readUTF();
			String domainBlackListJson = dis.readUTF();
			String keywordBlackListJson = dis.readUTF();
			String appWhiteListJson = dis.readUTF();
			
			ObjectMapper mapper = new ObjectMapper();

			Map<String, Object> quietHour = mapper.readValue(quietHourJson, Map.class);
			List<String> domainBlackList = mapper.readValue(domainBlackListJson, List.class);
			List<String> keywordBlackList = mapper.readValue(keywordBlackListJson, List.class);
			List<String> appWhiteList = mapper.readValue(appWhiteListJson, List.class);
			
			writeOrCreateFileString("timeQuote.txt", String.valueOf(dailyTimeQuote));
			writeOrCreateFileMap("quietHour.txt", quietHour);
			writeOrCreateFileList("domain.txt", domainBlackList);
			writeOrCreateFileList("keyword.txt", keywordBlackList);
			writeOrCreateFileList("appWhite.txt", appWhiteList);
			
		}
		catch(Exception e)
		{
			
		}
		
	}
	
	private void writeOrCreateFileString(String fileName, String content)
	{
		try
		{
			File file = new File(fileName);
	        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false));

	        bw.write(content);
	        bw.flush();
	        bw.close();
		}
		catch(Exception e)
		{
			
		}	
	}
	
	private void writeOrCreateFileMap(String fileName, Map<String, Object> content)
	{
		try
		{
			File file = new File(fileName);
	        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false));

	        for (Map.Entry mapElement : content.entrySet()) {
                String key = (String)mapElement.getKey();

                // Finding the value
                String value = (String)mapElement.getValue();
                String str = key + " - " + value + "\n";
                bw.write(str);
            }
	        bw.flush();
	        bw.close();
		}
		catch(Exception e)
		{
			
		}	
	}
	
	private void writeOrCreateFileList(String fileName, List<String> content)
	{
		try
		{
			File file = new File(fileName);
	        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false));

	        for(String s : content)
        	{
	        	bw.write(s + "\n");
        	}
	        bw.flush();
	        bw.close();
		}
		catch(Exception e)
		{
			
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
