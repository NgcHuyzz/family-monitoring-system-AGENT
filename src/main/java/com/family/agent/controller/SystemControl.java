package com.family.agent.controller;

import com.family.agent.collector.AppCollector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

public class SystemControl {

    // Powershell full path để chạy chắc chắn dù chạy qua setup/vbs/service
    private static final String POWERSHELL =
            "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe";

    public static void KillActiveProcess() {
        try {
            System.out.println("=== [KillActiveProcess] START " + new Timestamp(System.currentTimeMillis()) + " ===");

            // 1) Lấy process đang active
            AppCollector appCollector = new AppCollector();
            String processName = appCollector.getActiveProcessName();

            System.out.println("[SystemControl] Active process raw: " + processName);

            if (processName == null) processName = "";
            processName = processName.trim();

            if (processName.isEmpty()) {
                System.out.println("[SystemControl] Khong tim duoc process dang active");
                return;
            }

            // 2) Chuẩn hóa: bỏ ".exe" nếu có
            String normalized = processName.toLowerCase();
            if (normalized.endsWith(".exe")) {
                normalized = normalized.substring(0, normalized.length() - 4);
            }

            // 3) Chặn kill mấy process hệ thống dễ gây lỗi/treo
            if (isSystemProcess(normalized)) {
                System.out.println("[SystemControl] Skip system process: " + normalized);
                return;
            }

            // 4) Browser multi-process => dùng taskkill /T để kill hết cây process
            if (isBrowser(normalized)) {
                String cmd = "cmd /c taskkill /F /IM " + normalized + ".exe /T";
                Process p = Runtime.getRuntime().exec(cmd);
                int exit = p.waitFor();

                String out = readAll(p);
                System.out.println("[SystemControl] taskkill exitCode=" + exit);
                if (!out.isBlank()) System.out.println(out);

                System.out.println("=== [KillActiveProcess] DONE (browser) " + normalized + " ===");
                return;
            }

            // 5) App thường => powershell Stop-Process
            // Stop-Process -Name cần name KHÔNG có .exe
            String psCmd = POWERSHELL
                    + " -NoProfile -ExecutionPolicy Bypass -Command "
                    + "\"Stop-Process -Name '" + escapeSingleQuote(normalized) + "' -Force\"";

            Process p = Runtime.getRuntime().exec(psCmd);
            int exit = p.waitFor();

            String out = readAll(p);
            System.out.println("[SystemControl] powershell exitCode=" + exit);
            if (!out.isBlank()) System.out.println(out);

            if (exit == 0) {
                System.out.println(new Timestamp(System.currentTimeMillis()));
                System.out.println("[SystemControl] Da tat ung dung: " + normalized);
            } else {
                System.out.println("[SystemControl] Kill fail, check log above. process=" + normalized);
            }

            System.out.println("=== [KillActiveProcess] DONE ===");

        } catch (Exception e) {
            System.out.println("[SystemControl] Loi khi tat ung dung: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void shutdownComputer() {
        try {
            Runtime.getRuntime().exec("shutdown /s /t 15");
        } catch (Exception e) {
            System.err.println("loi khi tat may " + e.getMessage());
        }
    }

    public static void restartComputer() {
        try {
            Runtime.getRuntime().exec("shutdown /r /t 0");
        } catch (Exception e) {
            System.err.println("loi khi restart " + e.getMessage());
        }
    }

    public static void lockScreen() {
        try {
            Runtime.getRuntime().exec("rundll32.exe user32.dll,LockWorkStation");
        } catch (Exception e) {
            System.err.println("Loi khi khoa man hinh " + e.getMessage());
        }
    }

    // =================== helpers ===================

    private static boolean isBrowser(String p) {
        return "chrome".equals(p) || "msedge".equals(p) || "firefox".equals(p)
                || "brave".equals(p) || "opera".equals(p);
    }

    private static boolean isSystemProcess(String p) {
        // tuỳ bạn thêm bớt
        return "explorer".equals(p)
                || "dwm".equals(p)
                || "csrss".equals(p)
                || "winlogon".equals(p)
                || "services".equals(p)
                || "lsass".equals(p)
                || "svchost".equals(p)
                || "system".equals(p)
                || "idle".equals(p);
    }

    private static String escapeSingleQuote(String s) {
        // cho powershell string single-quote
        return s.replace("'", "''");
    }

    private static String readAll(Process p) {
        StringBuilder sb = new StringBuilder();
        // stdout
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
        } catch (Exception ignored) {}
        // stderr
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
        } catch (Exception ignored) {}
        return sb.toString().trim();
    }
}
