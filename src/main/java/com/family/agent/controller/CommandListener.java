package com.family.agent.controller;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.UUID;

public class CommandListener extends Thread {
    private final Socket soc;
    private String deviceID;
    public CommandListener(Socket soc) {
        this.soc = soc;
        this.deviceID = getOrCreateDeviceID();
    }

    @Override
    public void run() {
        System.out.println("Starting CommandListener...");

        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(soc.getOutputStream()));

            // thông báo Agent đã online
//            out.println("{\"status\":\"ONLINE\"}");
            dos.writeUTF(deviceID);
			dos.flush();

            
            while (true) {
                String action = dis.readUTF();
                System.out.println(new Timestamp(System.currentTimeMillis()));
//                JSONObject json = new JSONObject(msg);
//                String action = json.getString("action");   // chỉ cần action

                switch (action) {
                    case "KILL_APP":
                        System.out.println("[Agent] Executing KILL_APP");
                        SystemControl.KillActiveProcess();  // không cần target
                        break;

                    case "SHUTDOWN":
                        System.out.println("[Agent] Executing SHUTDOWN");
                        SystemControl.shutdownComputer();
                        break;

                    case "RESTART":
                        System.out.println("[Agent] Executing RESTART");
                        SystemControl.restartComputer();
                        break;

                    case "LOCKSCREEN":
                        System.out.println("[Agent] Executing LOCKSCREEN");
                        SystemControl.lockScreen();
                        break;

                    default:
                        System.out.println("[Agent] Unknown action " + action);
                }
            }

        } catch (Exception e) {
            System.out.println("[Agent] CommandListener error: " + e.getMessage());
            e.printStackTrace();
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
