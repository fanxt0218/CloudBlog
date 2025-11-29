package com.cloudblog.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

@Slf4j
@Configuration
public class RAGConfig {

    /**
     * 向量存储
     */
    @Bean
    public VectorStore getVectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    /**
     * 预加载
     */
    @Bean
    public CommandLineRunner preload(EmbeddingModel embeddingModel, VectorStore vectorStore) {
        log.info("开始预加载");
        Resource targetResource = new ClassPathResource("rag/preload.txt");
        return args -> vectorStore.write(new TokenTextSplitter().transform(new TextReader(targetResource).read()));
    }
}
