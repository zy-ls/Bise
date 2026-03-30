package com.liutong.study.controller;

import com.liutong.study.common.Result;
import com.liutong.study.entity.SensitiveWord;
import com.liutong.study.mapper.SensitiveWordMapper;
import com.liutong.study.utils.SensitiveWordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 🛡️ 敏感词库管理 (供 Admin 后台使用)
 */
@RestController
@RequestMapping("/admin/word")
public class SensitiveWordController {

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    /**
     * 获取全部敏感词
     */
    @GetMapping("/list")
    public Result<List<SensitiveWord>> list() {
        return Result.success(sensitiveWordMapper.selectList(null));
    }

    /**
     * 添加敏感词，并动态刷新 DFA 引擎
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody SensitiveWord word) {
        sensitiveWordMapper.insert(word);
        refreshDFA(); // 💡 核心：添加后立刻刷新内存中的字典树
        return Result.success("添加成功，系统安全墙已实时更新");
    }

    /**
     * 删除敏感词，并动态刷新 DFA 引擎
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        sensitiveWordMapper.deleteById(id);
        refreshDFA(); // 💡 核心：删除后立刻刷新
        return Result.success("删除成功，系统安全墙已实时更新");
    }

    // 重新从数据库拉取词库，并覆盖内存中的 DFA 树
    private void refreshDFA() {
        List<SensitiveWord> words = sensitiveWordMapper.selectList(null);
        List<String> wordList = words.stream().map(SensitiveWord::getWord).collect(Collectors.toList());
        SensitiveWordUtil.initSensitiveWordMap(wordList);
    }
}