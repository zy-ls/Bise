package com.liutong.study.config;

import com.liutong.study.entity.SensitiveWord;
import com.liutong.study.mapper.SensitiveWordMapper;
import com.liutong.study.utils.SensitiveWordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 🛡️ 服务启动时，自动加载敏感词库到 DFA 字典树中
 */
@Slf4j
@Component
public class SensitiveWordInitRunner implements ApplicationRunner {

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1. 从数据库查出所有敏感词
        List<SensitiveWord> words = sensitiveWordMapper.selectList(null);
        // 2. 提取出纯字符串 List
        List<String> wordList = words.stream().map(SensitiveWord::getWord).collect(Collectors.toList());
        // 3. 注入到 DFA 引擎
        SensitiveWordUtil.initSensitiveWordMap(wordList);

        log.info("====== 🛡️ 敏感词库(DFA) 加载完毕，共装载 {} 个违规词 ======", wordList.size());
    }
}