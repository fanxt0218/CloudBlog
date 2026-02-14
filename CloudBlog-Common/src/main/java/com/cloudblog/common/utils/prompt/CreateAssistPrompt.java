package com.cloudblog.common.utils.prompt;

public class CreateAssistPrompt extends GenerateContentPrompt<CreateAssistPrompt>{

    private String originalPrompt = """
            你是一个写作助手，你需要根据用户要求协助用户进行创作
            你擅长各种领域的文章的创作
            你擅长大纲生成、代码生成、以及对用户的文章内容提出修改和优化建议
            """;

    public CreateAssistPrompt() {}

    public CreateAssistPrompt(String prompt) {
        this.originalPrompt = prompt;
    }
    @Override
    public CreateAssistPrompt addRule(String prompt) {
        this.originalPrompt += prompt + "\n";
        return this;
    }

    @Override
    public String getPrompt() {
        return originalPrompt;
    }

}
