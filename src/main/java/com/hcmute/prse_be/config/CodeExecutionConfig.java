package com.hcmute.prse_be.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "code-execution")
public class CodeExecutionConfig {
    private String tempDirectory = System.getProperty("java.io.tmpdir") + "/code-execution";
    private Map<String, LanguageConfig> languages = Map.of(
            "python", new LanguageConfig("python:3.11-alpine", "py", null, "python", 10000),

            // ✅ Đổi thành ubuntu:22.04 và install GCC
            "cpp", new LanguageConfig("ubuntu:22.04", "cpp",
                    "apt-get update && apt-get install -y g++ && g++ -o /tmp/program /tmp/code.cpp",
                    "/tmp/program", 15000),

            "java", new LanguageConfig("amazoncorretto:17-alpine", "java",
                    "javac -d /tmp /tmp/Main.java",
                    "java -cp /tmp Main", 15000)
    );

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LanguageConfig {
        private String image;
        private String extension;
        private String compileCommand;
        private String runCommand;
        private long timeout;
    }
}
