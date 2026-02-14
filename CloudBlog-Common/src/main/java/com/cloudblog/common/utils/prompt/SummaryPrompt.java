package com.cloudblog.common.utils.prompt;

import org.elasticsearch.search.aggregations.metrics.Sum;

public class SummaryPrompt extends GenerateContentPrompt<SummaryPrompt> {

    String originalPrompt = """
            你是一个善于总结内容并生成简要总结的AI助手
            你需要根据用户的要求完成内容的总结
            """;

    public SummaryPrompt() {
    }

    public SummaryPrompt(String originalPrompt) {
        this.originalPrompt = originalPrompt;
    }

    @Override
    public SummaryPrompt addRule(String prompt) {
        this.originalPrompt += prompt + "\n";
        return this;
    }

    @Override
    public String getPrompt() {
        return originalPrompt;
    }

    public void setOriginalPrompt(String originalPrompt) {
        this.originalPrompt = originalPrompt;
    }
}
