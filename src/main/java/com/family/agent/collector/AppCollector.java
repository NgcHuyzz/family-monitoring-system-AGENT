package com.family.agent.collector;

import com.family.agent.model.LogEntry;

import java.io.BufferedReader;
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
                    return;
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
            String scriptPath = getClass()
                    .getClassLoader()
                    .getResource("get_active_window.ps1")
                    .getPath()
                    .replaceFirst("^/(.:/)", "$1"); // fix đường dẫn trên Windows

            Process process = Runtime.getRuntime().exec(new String[]{
                    "powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", scriptPath
            });

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            reader.close();

            String title = output.toString().trim();
            if (title.isEmpty()) {
                System.out.println("[DEBUG] Không có output từ PowerShell");
                return null;
            }

            return title;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getActiveProcessName() {
        System.out.println("[AC] BẮT ĐẦU getActiveProcessName()");

        try {
            // --- Load resource ---
            URL url = getClass().getClassLoader().getResource("get_active_process.ps1");
            if (url == null) {
                System.out.println("[AC] KHÔNG TÌM THẤY FILE get_active_process.ps1 TRONG RESOURCES");
                return null;
            }

            // --- Convert URL -> file path chuẩn ---
            String scriptPath = Paths.get(url.toURI()).toString();
            System.out.println("[AC] scriptPath = " + scriptPath);

            // --- Start PowerShell ---
            ProcessBuilder pb = new ProcessBuilder(
                    "powershell",
                    "-NoProfile",
                    "-ExecutionPolicy", "Bypass",
                    "-File", scriptPath
            );
            pb.redirectErrorStream(false);
            Process process = pb.start();

            System.out.println("[AC] ĐÃ START PowerShell");

            // --- Read stdout ---
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "UTF-8")
            );
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[AC][stdout] " + line);
                output.append(line).append("\n");
            }

            // --- Read stderr ---
            BufferedReader errReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), "UTF-8")
            );
            String errLine;
            while ((errLine = errReader.readLine()) != null) {
                System.out.println("[AC][stderr] " + errLine);
            }

            int exitCode = process.waitFor();
            System.out.println("[AC] exitCode = " + exitCode);

            // --- Output ---
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