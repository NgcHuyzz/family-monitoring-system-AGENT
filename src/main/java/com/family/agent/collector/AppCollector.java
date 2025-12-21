package com.family.agent.collector;

import com.family.agent.model.LogEntry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
//import com.sun.jna.Native;
//import com.sun.jna.platform.win32.User32;
//import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AppCollector implements Runnable {
//    private static final Logger log = LoggerFactory.getLogger(AppCollector.class);
    public LogEntry lastLog = null;
    private String lastApp = null;
    private Timestamp startTime = null;

    @Override
    public void run() {
        while (true) {
            try {
                String currentApp = getActiveWindowTitle();

                if (currentApp == null || currentApp.isEmpty()) {
                    System.out.println("Khong tim thay ung dung dang su dung");
                    Thread.sleep(3000);
                    continue;
                }

                // lan dau khi chay
                if (lastApp == null) {
                    lastApp = currentApp;
                    startTime = new Timestamp(System.currentTimeMillis());
                    System.out.println("dang su dung " + lastApp);
                    continue;
                }

                // neu nguoi dung chuyen app khac
                if (!currentApp.equals(lastApp)) {
                    Timestamp endTime = new Timestamp(System.currentTimeMillis());
                    LogEntry log = new LogEntry("app", lastApp, startTime, endTime);
                    log.print();

                    lastLog = log;

                    lastApp = currentApp;
                    startTime = new Timestamp(System.currentTimeMillis());
                    System.out.println("Chuyen sang ung dung moi " + currentApp);
                }

                Thread.sleep(2000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    private String getActiveWindowTitle() {
//        char[] buffer = new char[1024];
//        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
//        if (hwnd == null) return null;
//
//        User32.INSTANCE.GetWindowText(hwnd, buffer, 1024);
//        return Native.toString(buffer);
        try {
            // Lấy đường dẫn tuyệt đối của file ps1 trong resources
        	InputStream in = getClass().getClassLoader().getResourceAsStream("get_active_window.ps1");
            if (in == null) {
                System.out.println("[DEBUG] Không thấy get_active_window.ps1");
                return null;
            }

            // extract ra file tạm
            java.nio.file.Path temp = java.nio.file.Files.createTempFile("active", ".ps1");
            temp.toFile().deleteOnExit();
            java.nio.file.Files.copy(in, temp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // powershell tuyệt đối
            String pwsh = System.getenv("SystemRoot") + "\\System32\\WindowsPowerShell\\v1.0\\powershell.exe";

            ProcessBuilder pb = new ProcessBuilder(
                    pwsh, "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", temp.toString()
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");

            p.waitFor();
            String result = sb.toString().trim();

            if (result.isEmpty()) {
                System.out.println("[DEBUG] PowerShell empty");
                return null;
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getActiveProcessName() {
    	System.out.println("[AC] BẮT ĐẦU getActiveProcessName()");
        try {
            URL url = getClass().getClassLoader().getResource("get_active_process.ps1");
            if (url == null) {
                System.out.println("[AC] KHÔNG TÌM THẤY FILE get_active_process.ps1 TRONG RESOURCES");
                return null;
            }

            // Extract resource -> file tạm (dùng URL -> Path)
            java.nio.file.Path temp = java.nio.file.Files.createTempFile("get_active_process", ".ps1");
            temp.toFile().deleteOnExit();
            java.nio.file.Files.copy(url.openStream(), temp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            String pwsh = System.getenv("SystemRoot") + "\\System32\\WindowsPowerShell\\v1.0\\powershell.exe";
            ProcessBuilder pb = new ProcessBuilder(
                    pwsh, "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", temp.toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            System.out.println("[AC] ĐÃ START PowerShell");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), java.nio.charset.StandardCharsets.UTF_8)
            );
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[AC][stdout] " + line);
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            System.out.println("[AC] exitCode = " + exitCode);

            String processName = output.toString().trim();
            System.out.println("[AC] processName = [" + processName + "]");
            return processName.isEmpty() ? null : processName;

        } catch (Exception e) {
            System.out.println("[AC] EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public String getCurrentApp() {
        return lastApp;
    }

}