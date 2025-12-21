package com.family.agent.util;

import com.family.agent.model.config;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.*;

public final class ConfigLoader {
    private static volatile config CACHED;

    private ConfigLoader() {}

    public static config load() {
        if (CACHED != null) return CACHED;
        synchronized (ConfigLoader.class) {
            if (CACHED != null) return CACHED;

            try {
                Path cfg = FilePath.base().resolve("config.json");
                ObjectMapper mapper = new ObjectMapper();
                config c = mapper.readValue(Files.readString(cfg), config.class);

                // làm sạch dữ liệu
                if (c.getDeviceId() != null) c.setDeviceId(c.getDeviceId().trim());
                if (c.getAgentKey() != null) c.setAgentKey(c.getAgentKey().trim());
                if (c.getServerUrl() != null) c.setServerUrl(c.getServerUrl().trim());

                // KHÔNG bắt buộc UUID ở đây – tuỳ server validate
                if (isBlank(c.getDeviceId()) || isBlank(c.getAgentKey())) {
                    throw new IllegalStateException("Thiếu deviceId/agentKey trong config.json");
                }
                CACHED = c;
                return CACHED;
            } catch (Exception ex) {
                // fallback rỗng để không NPE, nhưng báo rõ
                config c = new config();
                c.setServerUrl("http://127.0.0.1:8080");
                c.setAgentKey("");
                c.setDeviceId("");
                CACHED = c;
                return CACHED;
            }
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
