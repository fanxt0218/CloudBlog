package com.cloudblog.common.utils.prompt;

public abstract class GenerateContentPrompt<T extends GenerateContentPrompt<T>> {

    String originalPrompt = "";

    public String getPrompt() {
        return originalPrompt;
    }

    public abstract T addRule(String prompt);

}
