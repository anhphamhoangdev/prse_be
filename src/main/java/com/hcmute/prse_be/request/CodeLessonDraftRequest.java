package com.hcmute.prse_be.request;

public class CodeLessonDraftRequest {
    private String language;
    private String content;
    private String initialCode;
    private String solutionCode;
    private String expectedOutput;
    private String hints;
    private String difficultyLevel;
    private TestCase testCase;

    // Getters and setters
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getInitialCode() { return initialCode; }
    public void setInitialCode(String initialCode) { this.initialCode = initialCode; }
    public String getSolutionCode() { return solutionCode; }
    public void setSolutionCode(String solutionCode) { this.solutionCode = solutionCode; }
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    public String getHints() { return hints; }
    public void setHints(String hints) { this.hints = hints; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public TestCase getTestCase() { return testCase; }
    public void setTestCase(TestCase testCase) { this.testCase = testCase; }
}
