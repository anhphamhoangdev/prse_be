package com.hcmute.prse_be.request;

public class TestCase {
    private String input;
    private String expectedOutput;
    private String description;

    // Getters and setters
    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}