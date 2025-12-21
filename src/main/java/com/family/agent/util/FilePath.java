package com.family.agent.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePath {

    private static final Path BASE = Paths.get(System.getenv("ProgramData"), "FamilyAgent");

    static {
        try {
            Files.createDirectories(BASE);
            Files.createDirectories(BASE.resolve("logs"));
            Files.createDirectories(BASE.resolve("lists"));
            Files.createDirectories(BASE.resolve("policy"));
        } catch (Exception e) {}
    }

    public static Path base() {
        return BASE;
    }

    public static Path file(String name) {
        return BASE.resolve(name);  // ví dụ name = "keyword.txt"
    }

    public static Path listFile(String name) {
        return BASE.resolve("lists").resolve(name);
    }

    public static Path policyFile(String name) {
        return BASE.resolve("policy").resolve(name);
    }
}
