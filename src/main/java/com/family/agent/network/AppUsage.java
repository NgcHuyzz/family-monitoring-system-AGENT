package com.family.agent.network;

import com.family.agent.collector.AppCollector;
import com.family.agent.model.LogEntry;
import com.family.agent.scheduler.Scheduler;
import com.family.agent.util.AESUtil;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.UUID;

public class AppUsage extends Thread{
    private Socket soc;
    private String deviceID;
//    private LogEntry log;
    private DataOutputStream dos;

    public AppUsage (Socket soc)
    {
        this.soc = soc;
//        this.log = log;
        this.deviceID = getDeviceID();
    }

    @Override
    public void run()
    {
        try {
            dos = new DataOutputStream(new BufferedOutputStream(soc.getOutputStream()));
            dos.writeUTF(deviceID);
            dos.flush();

            Scheduler scheduler = new Scheduler();
            scheduler.start();

            AppCollector appCollector = scheduler.getAppCollector();
            // Đợi 3s để collector thu thập dữ liệu đầu tiên
            Thread.sleep(3000);

            // Gửi log của ứng dụng hiện tại ngay sau khi khởi động
            if (appCollector != null && appCollector.getCurrentApp() != null) {
                String currentApp = appCollector.getCurrentApp();
                Timestamp now = new Timestamp(System.currentTimeMillis());
                LogEntry initialLog = new LogEntry(
                        "app",
                        currentApp,
                        new Timestamp(now.getTime() - 2000),
                        now
                );
                sendLogTOServer(initialLog);
            }

            LogEntry lastSent = null;

            while (true)
            {
                try{
                    LogEntry current = appCollector.lastLog;
                    if (current != null && (lastSent == null || !current.toString().equals(lastSent.toString())))
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
//            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(soc.getOutputStream()));
//            dos.writeUTF(deviceID);
            if (dos == null) {
                System.err.println("[Client] stream chua duoc khoi tao");
                return;
            }
            dos.writeUTF(log.getAppName());
            dos.writeLong(log.getStartTime().getTime());
            dos.writeLong(log.getEndTime().getTime());
            dos.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
//        try {
//            if (dos == null) {
//                System.err.println("[Client] stream chua duoc khoi tao");
//                return;
//            }
//
//            // 🔹 Định dạng thời gian chuẩn
//            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//
//            // 🔹 Gộp thành 1 chuỗi hoàn chỉnh
//            String payload = log.getType() + "|" +
//                    log.getAppName() + "|" +
//                    sdf.format(log.getStartTime()) + "|" +
//                    sdf.format(log.getEndTime());
//
//            // 🔹 Mã hóa toàn bộ chuỗi payload
//            AESUtil.EncryptedData enc = AESUtil.encrypt(payload);
//
//            // 🔹 Gửi dữ liệu mã hóa sang server
//            dos.writeInt(enc.cipherText.length);
//            dos.write(enc.cipherText);
//            dos.writeInt(enc.iv.length);
//            dos.write(enc.iv);
//            dos.flush();
//
//            System.out.println("[Client] Đã mã hóa & gửi log (chuỗi đầy đủ): " + payload);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    private String getDeviceID() 
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
		}
		catch(Exception e)
		{
			
		}
		
		return "unknown";
	}
}
