package com.liutong.study.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🛡️ 基于 DFA (确定有穷自动机) 算法的敏感词过滤器
 * 极致性能，时间复杂度接近 O(N)
 */
public class SensitiveWordUtil {

    // 敏感词字典树 (Trie Tree)
    private static Map<Object, Object> sensitiveWordMap = null;

    /**
     * 1. 初始化敏感词库 (服务启动或管理员更新词库时调用)
     * @param wordList 数据库里查出来的所有敏感词
     */
    @SuppressWarnings("unchecked")
    public static void initSensitiveWordMap(List<String> wordList) {
        sensitiveWordMap = new HashMap<>(wordList.size());
        for (String word : wordList) {
            Map<Object, Object> currentMap = sensitiveWordMap;
            for (int i = 0; i < word.length(); i++) {
                char keyChar = word.charAt(i);
                Object wordMap = currentMap.get(keyChar);
                if (wordMap != null) {
                    currentMap = (Map<Object, Object>) wordMap;
                } else {
                    Map<Object, Object> newWordMap = new HashMap<>();
                    newWordMap.put("isEnd", "0");
                    currentMap.put(keyChar, newWordMap);
                    currentMap = newWordMap;
                }
                if (i == word.length() - 1) {
                    currentMap.put("isEnd", "1"); // 最后一个字符，标记为结束
                }
            }
        }
    }

    /**
     * 2. 检查文本中是否包含敏感词
     * @param text 用户提交的笔记正文或标题
     * @return 只要包含任何一个敏感词，立刻返回 true，不包含返回 false
     */
    @SuppressWarnings("unchecked")
    public static boolean containsSensitiveWord(String text) {
        if (sensitiveWordMap == null || sensitiveWordMap.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            Map<Object, Object> currentMap = sensitiveWordMap;
            int matchLength = 0;
            for (int j = i; j < text.length(); j++) {
                char word = text.charAt(j);
                currentMap = (Map<Object, Object>) currentMap.get(word);
                if (currentMap == null) {
                    break;
                }
                matchLength++;
                if ("1".equals(currentMap.get("isEnd"))) {
                    return true; // 发现完整敏感词！
                }
            }
        }
        return false;
    }
}