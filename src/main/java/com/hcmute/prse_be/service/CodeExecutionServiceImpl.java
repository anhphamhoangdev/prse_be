package com.hcmute.prse_be.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.hcmute.prse_be.config.CodeExecutionConfig;
import com.hcmute.prse_be.dtos.CodeExecutionRequestDto;
import com.hcmute.prse_be.dtos.CodeExecutionResponseDto;
import com.hcmute.prse_be.dtos.SupportedLanguageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CodeExecutionServiceImpl implements CodeExecutionService {

    private final CodeExecutionConfig config;
    private final DockerClient dockerClient;

    @Autowired
    public CodeExecutionServiceImpl(CodeExecutionConfig config) {
        this.config = config;
        // Tạo Docker client với cách mới
        DefaultDockerClientConfig clientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build();

        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(clientConfig.getDockerHost())
                .build();

        this.dockerClient = DockerClientImpl.getInstance(clientConfig, httpClient);
        initializeTempDirectory();
    }

    private void initializeTempDirectory() {
        try {
            Path tempPath = Paths.get(config.getTempDirectory());
            if (!Files.exists(tempPath)) {
                Files.createDirectories(tempPath);
            }
        } catch (IOException e) {
            log.error("Failed to create temp directory", e);
        }
    }

    @Override
    public CodeExecutionResponseDto executeCode(CodeExecutionRequestDto request) {
        long startTime = System.currentTimeMillis();
        String executionId = UUID.randomUUID().toString();

        try {
            // Validate input
            if (request.getCode() == null || request.getCode().trim().isEmpty()) {
                return createErrorResponse("Code cannot be empty", startTime);
            }

            CodeExecutionConfig.LanguageConfig langConfig = config.getLanguages().get(request.getLanguage().toLowerCase());
            if (langConfig == null) {
                return createErrorResponse("Unsupported language: " + request.getLanguage(), startTime);
            }

            // Create execution directory
            Path execDir = createExecutionDirectory(executionId);

            // Write code and input files
            writeCodeAndInputFiles(execDir, request, langConfig);

            // Execute in Docker
            CodeExecutionResponseDto result = executeInDocker(langConfig, execDir, request, startTime);

            // Check output if expected output is provided
            if (result.isSuccess() && request.getExpectedOutput() != null && !request.getExpectedOutput().trim().isEmpty()) {
                boolean isCorrect = checkOutput(result.getOutput(), request.getExpectedOutput());
                result.setIsCorrect(isCorrect);
                result.setActualOutput(result.getOutput());
                result.setExpectedOutput(request.getExpectedOutput());
            }

            // Cleanup
            cleanup(execDir);

            return result;

        } catch (Exception e) {
            log.error("Code execution failed", e);
            return createErrorResponse("Execution failed: " + e.getMessage(), startTime);
        }
    }

    private Path createExecutionDirectory(String executionId) throws IOException {
        Path execDir = Paths.get(config.getTempDirectory(), executionId);
        Files.createDirectories(execDir);
        return execDir;
    }

    private void writeCodeAndInputFiles(Path execDir, CodeExecutionRequestDto request,
                                        CodeExecutionConfig.LanguageConfig langConfig) throws IOException {
        // Write code file
        String fileName = "java".equalsIgnoreCase(request.getLanguage()) ?
                "Main.java" : "code." + langConfig.getExtension();
        Path codePath = execDir.resolve(fileName);
        Files.write(codePath, request.getCode().getBytes());

        // Write input file
        Path inputPath = execDir.resolve("input.txt");
        String input = request.getInput() != null ? request.getInput() : "";
        Files.write(inputPath, input.getBytes());
    }

    private CodeExecutionResponseDto executeInDocker(CodeExecutionConfig.LanguageConfig langConfig,
                                                     Path execDir, CodeExecutionRequestDto request,
                                                     long startTime) throws Exception {

        // Create container
        String fileName = "java".equalsIgnoreCase(request.getLanguage()) ?
                "Main.java" : "code." + langConfig.getExtension();

        // KHÔNG dùng autoRemove - để có thể lấy logs sau khi container finish
        HostConfig hostConfig = HostConfig.newHostConfig()
                .withMemory(128L * 1024 * 1024) // 128MB
                .withCpuQuota(50000L) // 50% CPU
                .withNetworkMode("none")
                .withBinds(Bind.parse(execDir.toString() + ":/tmp:rw"));
        // .withAutoRemove(true); // BỎ dòng này

        CreateContainerResponse container = dockerClient.createContainerCmd(langConfig.getImage())
                .withHostConfig(hostConfig)
                .withWorkingDir("/tmp")
                .withCmd("sh", "-c", buildCommand(langConfig, fileName))
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec();

        String containerId = container.getId();

        try {
            // Start container
            dockerClient.startContainerCmd(containerId).exec();

            // Wait for container with timeout
            WaitContainerResultCallback callback = new WaitContainerResultCallback();
            dockerClient.waitContainerCmd(containerId).exec(callback);

            boolean finished = callback.awaitCompletion(langConfig.getTimeout(), TimeUnit.MILLISECONDS);

            if (!finished) {
                // Kill container nếu timeout
                try {
                    dockerClient.killContainerCmd(containerId).exec();
                    log.warn("Container killed due to timeout: " + containerId);
                } catch (Exception e) {
                    log.warn("Failed to kill container: " + containerId, e);
                }
                return createErrorResponse("Code execution timed out", startTime);
            }

            // Get output sau khi container đã hoàn thành (nhưng chưa bị remove)
            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            ByteArrayOutputStream stderr = new ByteArrayOutputStream();

            try {
                dockerClient.logContainerCmd(containerId)
                        .withStdOut(true)
                        .withStdErr(true)
                        .exec(new LogCallback(stdout, stderr))
                        .awaitCompletion();

                String output = stdout.toString().trim();
                String error = stderr.toString().trim();

                long executionTime = System.currentTimeMillis() - startTime;

                if (!error.isEmpty()) {
                    return new CodeExecutionResponseDto(false, output, error, executionTime, 0, "error", null, null, null);
                }

                return new CodeExecutionResponseDto(true, output, "", executionTime, 0, "completed", null, null, null);

            } catch (Exception e) {
                log.warn("Failed to get container logs: " + containerId, e);
                return createErrorResponse("Failed to retrieve execution output", startTime);
            }

        } finally {
            // Manually remove container để cleanup
            try {
                dockerClient.removeContainerCmd(containerId)
                        .withForce(true) // Force remove ngay cả khi container đang chạy
                        .exec();
                log.debug("Container removed successfully: " + containerId);
            } catch (Exception e) {
                log.warn("Failed to remove container: " + containerId, e);
            }
        }
    }

    private String buildCommand(CodeExecutionConfig.LanguageConfig langConfig, String fileName) {
        if (langConfig.getCompileCommand() != null) {
            String compileCmd = langConfig.getCompileCommand()
                    .replace("/tmp/code.cpp", "/tmp/" + fileName)
                    .replace("/tmp/Main.java", "/tmp/" + fileName);
            return compileCmd + " && " + langConfig.getRunCommand() + " < /tmp/input.txt";
        }
        return langConfig.getRunCommand() + " /tmp/" + fileName + " < /tmp/input.txt";
    }

    private boolean checkOutput(String actualOutput, String expectedOutput) {
        if (actualOutput == null || expectedOutput == null) {
            return false;
        }

        // Normalize whitespace and line endings
        String normalizedActual = actualOutput.trim().replaceAll("\\s+", " ");
        String normalizedExpected = expectedOutput.trim().replaceAll("\\s+", " ");

        return normalizedActual.equals(normalizedExpected);
    }

    private void cleanup(Path directory) {
        try {
            Files.walk(directory)
                    .sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            log.warn("Failed to delete: " + path, e);
                        }
                    });
        } catch (IOException e) {
            log.error("Cleanup failed", e);
        }
    }

    private CodeExecutionResponseDto createErrorResponse(String error, long startTime) {
        long executionTime = System.currentTimeMillis() - startTime;
        return new CodeExecutionResponseDto(false, "", error, executionTime, 0, "error", null, null, null);
    }

    @Override
    public List<SupportedLanguageDto> getSupportedLanguages() {
        return Arrays.asList(
                new SupportedLanguageDto("Python", "python", "3.11", "py"),
                new SupportedLanguageDto("C++", "cpp", "GCC 11", "cpp"),
                new SupportedLanguageDto("Java", "java", "17", "java")
        );
    }

    @Override
    public boolean isHealthy() {
        try {
            CodeExecutionRequestDto testRequest = new CodeExecutionRequestDto();
            testRequest.setCode("print('Hello, World!')");
            testRequest.setLanguage("python");
            testRequest.setInput("");

            CodeExecutionResponseDto result = executeCode(testRequest);
            return result.isSuccess();
        } catch (Exception e) {
            log.error("Health check failed", e);
            return false;
        }
    }

    // Helper class for Docker log callback
    private static class LogCallback extends ResultCallback.Adapter<Frame> {
        private final ByteArrayOutputStream stdout;
        private final ByteArrayOutputStream stderr;

        public LogCallback(ByteArrayOutputStream stdout, ByteArrayOutputStream stderr) {
            this.stdout = stdout;
            this.stderr = stderr;
        }

        @Override
        public void onNext(Frame frame) {
            try {
                if (frame.getStreamType() == StreamType.STDOUT) {
                    stdout.write(frame.getPayload());
                } else if (frame.getStreamType() == StreamType.STDERR) {
                    stderr.write(frame.getPayload());
                }
            } catch (IOException e) {
                log.error("Error writing frame", e);
            }
        }
    }
}
