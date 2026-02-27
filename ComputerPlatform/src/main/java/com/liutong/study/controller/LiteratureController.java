package com.liutong.study.controller;

import com.liutong.study.common.Result;
import com.liutong.study.entity.Literature;
import com.liutong.study.service.ILiteratureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/literature")
public class LiteratureController {

    @Autowired
    private ILiteratureService literatureService;

    /**
     * 📚 获取文献列表
     */
    @GetMapping("/list")
    public Result<List<Literature>> list() {
        // 这里简单查所有，实际可能要分页
        List<Literature> list = literatureService.list();
        return Result.success(list);
    }

    /**
     * 📤 发布新文献 (表单提交)
     * 前端先调 /file/upload 拿到路径，再把路径和标题填到这里保存
     */
    @PostMapping("/save")
    public Result<String> save(@RequestBody Literature literature) {
        literature.setUploadTime(LocalDateTime.now());
        literature.setStatus(1);
        // 模拟当前登录用户ID
        literature.setUploaderId(1L);

        boolean success = literatureService.save(literature);
        return success ? Result.success("文献发布成功") : Result.error("发布失败");
    }

    /**
     * 🔍 获取详情
     */
    @GetMapping("/{id}")
    public Result<Literature> getDetail(@PathVariable Long id) {
        return Result.success(literatureService.getById(id));
    }
}