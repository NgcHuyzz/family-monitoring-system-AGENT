package com.family.agent.network;

import com.family.agent.collector.AppCollector;
import com.family.agent.model.LogEntry;
import com.family.agent.scheduler.Scheduler;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class AppUsage extends Thread{
    private Socket soc;
    private String deviceID;
//    private LogEntry log;

    public AppUsage (Socket soc)
    {
        this.soc = soc;
//        this.log = log;
        this.deviceID = getOrCreateDeviceID();
    }

    @Override
    public void run()
    {
        try {
            Scheduler scheduler = new Scheduler();
            scheduler.start();

            AppCollector appCollector = scheduler.getAppCollector();
            LogEntry lastSent = null;

            System.out.println("Bat dau log ung dung ");

            while (true)
            {
                try{
                    LogEntry current = appCollector.lastLog;
                    if (current != null && current != lastSent)
                    {
                        sendLogTOServer(current);
                        lastSent = current;
                    }
                    Thread.sleep(2000);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
//            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(soc.getOutputStream()));
//
//            //gui deviceID
//            dos.writeUTF(deviceID);
//            dos.flush();
//
//            //gui logentry
//            dos.writeUTF(log.toString());
//            dos.flush();
//
//            System.out.println("AppUsage Sent log: " + log.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void sendLogTOServer(LogEntry log)
    {
        try {
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(soc.getOutputStream()));
            dos.writeUTF(deviceID);
            dos.writeUTF(log.toString());
            dos.flush();

            System.out.println("Da gui log " + log.toString());
        }
        catch (Exception e)
        {
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
