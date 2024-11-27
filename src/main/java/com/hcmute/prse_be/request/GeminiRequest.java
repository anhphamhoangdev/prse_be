package com.hcmute.prse_be.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // Chỉ serialize các trường không null
public class GeminiRequest {
    @JsonProperty("contents")
    private List<Content> contents;

    @JsonProperty("generationConfig")
    private GenerationConfig generationConfig;

    @JsonProperty("safetySettings")
    private List<SafetySetting> safetySettings;

    public GeminiRequest(String text) {
        // Initialize contents
        this.contents = List.of(new Content(List.of(new Part(text))));

        // Set default generation config
        this.generationConfig = new GenerationConfig(
                0.7,    // temperature
                40,     // topK
                0.95,   // topP
                1024,   // maxOutputTokens
                List.of() // stopSequences
        );

        // Set default safety settings
        this.safetySettings = Arrays.asList(
                new SafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_MEDIUM_AND_ABOVE"),
                new SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_MEDIUM_AND_ABOVE"),
                new SafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_MEDIUM_AND_ABOVE"),
                new SafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_MEDIUM_AND_ABOVE")
        );
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Content {
        @JsonProperty("parts")
        private List<Part> parts;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Part {
        @JsonProperty("text")
        private String text;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GenerationConfig {
        @JsonProperty("temperature")
        private double temperature;

        @JsonProperty("topK")
        private int topK;

        @JsonProperty("topP")
        private double topP;

        @JsonProperty("maxOutputTokens")
        private int maxOutputTokens;

        @JsonProperty("stopSequences")
        private List<String> stopSequences;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SafetySetting {
        @JsonProperty("category")
        private String category;

        @JsonProperty("threshold")
        private String threshold;
    }
}
