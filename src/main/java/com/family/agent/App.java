package com.family.agent;

import java.nio.file.*;
import java.io.IOException;
import java.time.LocalDateTime;

import com.family.agent.network.Uploader;

public class App {

    public static void main(String[] args) {

        // 0) Fallback ProgramData
        String progData = System.getenv("ProgramData");
        if (progData == null || progData.isBlank()) {
            progData = "C:\\ProgramData";
        }

        // 1) Đường dẫn
        Path base = Paths.get(progData, "FamilyAgent");
        Path cfg  = base.resolve("config.json");
        Path log  = base.resolve("agent.log");
        Path err  = base.resolve("agent.err.txt");
        Path tmp  = base.resolve("tmp");

        // 2) Chuẩn bị thư mục + ép toàn bộ thư mục tạm
        try {
            Files.createDirectories(base);
            Files.createDirectories(tmp);

            // Ép JVM + native libs dùng thư mục tmp
            System.setProperty("java.io.tmpdir", tmp.toString());
            System.setProperty("jnativehook.lib.location", tmp.toString());
            System.setProperty("jnativehook.lib.path", tmp.toString());
            System.setProperty("org.jnativehook.lib.location", tmp.toString());
            System.setProperty("jna.tmpdir", tmp.toString());
        } catch (IOException ioe) {
            hardFail(err, "FATAL mkdir tmp failed: " + ioe);
        }

     // 3) Config: ưu tiên config.json đi kèm app (thư mục chạy), nếu chưa có ở ProgramData
        try {
            if (!Files.exists(cfg)) {

                Path bundledCfg = null;

                // (1) Thư mục chứa file đang chạy (jar/classes)
                try {
                    Path runPath = Paths.get(App.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI());

                    Path runDir = Files.isDirectory(runPath) ? runPath : runPath.getParent();
                    if (runDir != null) {
                        Path candidate = runDir.resolve("config.json");
                        if (Files.exists(candidate)) bundledCfg = candidate;
                    }
                } catch (Exception ignored) {}

                // (2) Working directory thật của process (không dựa vào user.dir đã bị set)
                if (bundledCfg == null) {
                    Path candidate = Paths.get("").toAbsolutePath().resolve("config.json");
                    if (Files.exists(candidate)) bundledCfg = candidate;
                }

                if (bundledCfg != null) {
                    Files.copy(bundledCfg, cfg, StandardCopyOption.REPLACE_EXISTING);
                    logLine(log, err, "Copied bundled config.json from: " + bundledCfg + " -> " + cfg);
                } else {
                    String defaultCfg = "{\n" +
                            "  \"serverUrl\": \"http://127.0.0.1:8080\",\n" +
                            "  \"agentKey\": \"hehe\",\n" +
                            "  \"deviceId\": \"hehe\"\n" +
                            "}\n";
                    Files.writeString(cfg, defaultCfg);
                    logLine(log, err, "Created default config.json at: " + cfg + " (no bundled config found)");
                }
            } else {
                logLine(log, err, "Using existing config.json at: " + cfg);
            }
        } catch (Exception e) {
            logLine(log, err, "Init error: " + e.getMessage());
        }


        // 4) Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                logLine(log, err, "Agent shutting down...")
        ));

        // 5) Chạy tác vụ chính
        try {
            logLine(log, err, "Agent starting...");
            Uploader u = new Uploader();
            u.start();
            logLine(log, err, "Uploader started.");
        } catch (Throwable t) {
            logLine(log, err, "Start error: " + t);
        }
    }

    // Ghi log
    private static void logLine(Path log, Path err, String line) {
        try {
            Files.createDirectories(log.getParent());
            Files.writeString(
                    log,
                    LocalDateTime.now() + " | " + line + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            try {
                Files.createDirectories(err.getParent());
                Files.writeString(
                        err,
                        LocalDateTime.now() + " | LOG FAIL: " + e + " | ORIG: " + line + System.lineSeparator(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND
                );
            } catch (IOException ignored) {}
        }
    }

    // Ghi lỗi fatal khi khởi tạo thư mục
    private static void hardFail(Path err, String msg) {
        try {
            Files.createDirectories(err.getParent());
            Files.writeString(
                    err,
                    LocalDateTime.now() + " | " + msg + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND
            );
        } catch (IOException ignored) {}
    }
}
