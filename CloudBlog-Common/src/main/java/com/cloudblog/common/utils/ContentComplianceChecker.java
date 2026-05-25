package com.cloudblog.common.utils;


import com.cloudblog.common.mapper.ErrorMapper;
import com.cloudblog.common.pojo.DoMain.PostForbiddenWords;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class ContentComplianceChecker {

    private static final int MIN_MATCH_LENGTH = 2;
    private static final char END_FLAG = '\u0000';
    private static final Map<Character, Object> sensitiveWordMap = new HashMap<>();
    private static volatile boolean initialized = false;

    // 借用这个mapper
    @Autowired
    private ErrorMapper errorMapper;

    @PostConstruct
    public void init() {
        loadSensitiveWords();
        initialized = true;
    }

    /**
     * 检查文章内容是否合规
     * @param content 文章内容
     * @return 检测结果对象
     */
    public ComplianceResult checkCompliance(String content) {
        ComplianceResult result = new ComplianceResult();

        if (!initialized) {
            log.warn("敏感词库未初始化，默认返回合规");
            result.setCompliant(true);
            return result;
        }

        if (content == null || content.isEmpty()) {
            result.setCompliant(true);
            return result;
        }

        List<String> foundWords = findSensitiveWords(content);
        result.setFoundWords(foundWords);
        result.setCompliant(foundWords.isEmpty());
        result.setViolationCount(foundWords.size());

        if (!foundWords.isEmpty()) {
            result.setMessage("检测到 " + foundWords.size() + " 个违规词");
        } else {
            result.setMessage("内容合规");
        }

        return result;
    }

    /**
     * 查找文本中所有的敏感词
     * @param text 待检测文本
     * @return 敏感词列表
     */
    private List<String> findSensitiveWords(String text) {
        Set<String> wordSet = new HashSet<>();

        for (int i = 0; i < text.length(); i++) {
            int length = checkWord(text, i);
            if (length >= MIN_MATCH_LENGTH) {
                String word = text.substring(i, i + length);
                wordSet.add(word);
            }
        }

        return new ArrayList<>(wordSet);
    }

    /**
     * 从指定位置检查敏感词
     * @return 匹配长度，0表示未匹配
     */
    private int checkWord(String text, int beginIndex) {
        if (sensitiveWordMap.isEmpty()) {
            return 0;
        }

        int matchLength = 0;
        int lastEndPos = -1;
        Map<Character, Object> currentMap = sensitiveWordMap;

        for (int i = beginIndex; i < text.length(); i++) {
            char word = text.charAt(i);
            currentMap = (Map<Character, Object>) currentMap.get(word);

            if (currentMap == null) {
                break;
            }

            matchLength++;

            if (currentMap.containsKey(END_FLAG)) {
                lastEndPos = matchLength;
            }
        }

        return lastEndPos >= MIN_MATCH_LENGTH ? lastEndPos : 0;
    }

    /**
     * 加载敏感词库（可以从数据库/Redis/文件加载）
     */
    private void loadSensitiveWords() {
        List<String> words = loadSensitiveWordsFromSource();
        for (String word : words) {
            addWord(word);
        }
        log.info("敏感词库加载完成，共{}个敏感词", words.size());
    }

    /**
     * 从数据源加载敏感词
     * TODO: 根据实际需求修改数据源
     */
    private List<String> loadSensitiveWordsFromSource() {
        List<String> words = new ArrayList<>();

        // 示例：从Redis加载
        // words = redisUtil.lGet("sensitive_words", 0, -1);

        // 示例：从数据库加载
        // words = sensitiveWordMapper.selectEnabledWords();

        // 取出word字段
        words = errorMapper.getSensitiveWords().stream().map(PostForbiddenWords::getWord).toList();

        return words;
    }

    /**
     * 添加敏感词到Trie树
     */
    private void addWord(String word) {
        if (word == null || word.length() < MIN_MATCH_LENGTH) {
            return;
        }

        Map<Character, Object> currentMap = sensitiveWordMap;
        for (char c : word.toCharArray()) {
            currentMap = (Map<Character, Object>) currentMap.computeIfAbsent(c, k -> new HashMap<>());
        }
        currentMap.put(END_FLAG, true);
    }

    /**
     * 重新加载敏感词库
     */
    public void reloadSensitiveWords() {
        sensitiveWordMap.clear();
        loadSensitiveWords();
    }

    /**
     * 检测结果对象
     */
    @Data
    public static class ComplianceResult {
        /**
         * 是否合规
         */
        private boolean compliant;

        /**
         * 检测到的违规词列表
         */
        private List<String> foundWords = new ArrayList<>();

        /**
         * 违规词数量
         */
        private int violationCount;

        /**
         * 检测结果消息
         */
        private String message;
    }
}
