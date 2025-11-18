package com.family.agent.network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.Socket;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.family.agent.collector.AppCollector;
import com.family.agent.controller.PolicyChecker;
import com.family.agent.model.LogEntry;
import com.family.agent.model.PolicyEntry;
import com.family.agent.scheduler.Scheduler;

public class Alert extends Thread {
	private Socket soc;
	private String deviceID;
	private PolicyEntry policyConfig;
    private PolicyChecker policyChecker;
	
	private long dailyUsedMillis = 0;
    private LocalDate currentDate = LocalDate.now();
    DataOutputStream dos;
	public Alert(Socket soc)
	{
		this.soc = soc;
		this.deviceID = getOrCreateDeviceID();
		try
		{
			this.policyConfig = PolicyEntry.loadFromFolder();
			this.policyChecker = new PolicyChecker(policyConfig);			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		try
		{
			dos = new DataOutputStream(new BufferedOutputStream(soc.getOutputStream()));
            dos.writeUTF(deviceID);
            dos.flush();

            Scheduler scheduler = new Scheduler();
            scheduler.start();

            AppCollector appCollector = scheduler.getAppCollector();
            // Đợi 3s để collector thu thập dữ liệu đầu tiên
            Thread.sleep(3000);
            
            LogEntry lastChecked = null;

            // 3. Vòng lặp kiểm tra liên tục
            while (true) {
                try {
                    LogEntry current = appCollector.lastLog;

                    if (current != null &&
                            (lastChecked == null || !current.toString().equals(lastChecked.toString()))) {

                        handlePolicy(current);
                        lastChecked = current;
                    }

                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
		}
		catch(Exception e)
		{
			
		}
	}
	
	private void handlePolicy(LogEntry log)
	{
		LocalDate today = LocalDate.now();
        if (!today.equals(currentDate)) 
        {
            currentDate = today;
            dailyUsedMillis = 0;
        }
        
        String appName = log.getAppName();
        long duration = log.getEndTime().getTime() - log.getStartTime().getTime();
        if (duration < 0) duration = 0;

        dailyUsedMillis += duration;
        int usedMinutes = (int) (dailyUsedMillis / 60000L);
        
        if (policyChecker.isOverDailyQuota(usedMinutes)) 
            sendAlert("OVER_DAILY_QUOTA","Tong thoi gian su dung hom nay: " + usedMinutes + " phut (quota: " + policyChecker.getDailyQuotaMinutes() + " phut)");
        
        if (policyChecker.isDomainBlocked(appName)) 
            sendAlert("DOMAIN_BLOCKED", "Phat hien app/game cam: " + appName);
        
        DayOfWeek dow = today.getDayOfWeek();
        LocalTime now = LocalTime.now();

        if (policyChecker.isInQuietTime(dow, now)) 
            if (!policyChecker.isAppWhitelisted(appName)) 
                sendAlert("QUIET_HOUR_APP_NOT_WHITELISTED", "Dang trong gio im lang, app khong nam trong whitelist: " + appName);
	}
	
	private void sendAlert(String type, String message)
	{
		try
		{
			dos.writeUTF(type);
            dos.writeUTF(message);
            dos.writeLong(System.currentTimeMillis());
            dos.flush();
            System.out.println("Phat hien");
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
