package com.family.agent.controller;

import com.family.agent.collector.AppCollector;

import java.io.IOException;

public class SystemControl {
    public static void KillActiveProcess()
    {
        try {
            System.out.println("hihihi");
            AppCollector appCollector = new AppCollector();
            String processName = appCollector.getActiveProcessName();
            System.out.println("[SystemControl] Active process: " + processName);
            if (processName == null || processName.isEmpty()) {
                System.out.println("[SystemControl] Khong tim duoc process dang active");
                return;
            }
            String command = String.format(
                    "powershell -Command \"Stop-Process -Name '%s' -Force\"",
                    processName
            );

            Process process = null;
            try {
                process = Runtime.getRuntime().exec(command);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                process.waitFor();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            System.out.println("da tat ung dung" + processName);
        } catch (Exception e)
        {
            System.out.println("Loi khi tat ung dung " + e.getMessage());
        }
    }

    public static void shutdownComputer()
    {
        try {
            Runtime.getRuntime().exec("shutdown /s /t 15");
        } catch (Exception e)
        {
            System.err.println("loi khi tat may " + e.getMessage());
        }
    }

    public static void restartComputer() {
        try {
            Runtime.getRuntime().exec("shutdown /r /t 0");
        } catch (Exception e)
        {
            System.err.println("loi khi restart " + e.getMessage());
        }
    }

    public static void lockScreen()
    {
        try{
            Runtime.getRuntime().exec("rundll32.exe user32.dll,LockWorkStation");
        }
        catch (Exception e)
        {
            System.err.println("Loi khi khoa man hinh " + e.getMessage());
        }
    }
}
