package com.liutong.study.controller;

import cn.hutool.http.HtmlUtil; // 引入 Hutool 处理 HTML
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.Info;
import com.liutong.study.service.IInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.liutong.study.entity.User;
import com.liutong.study.service.IUserService;

import java.util.List;

/**
 * 笔记信息控制器
 */
@RestController
@RequestMapping("/note-info")
public class InfoController {

    @Autowired
    private com.liutong.study.repository.NoteSearchRepository noteSearchRepository;

    @Autowired
    private IInfoService infoService;

    @Autowired
    private IUserService userService;

    /**
     * 获取我的笔记列表
     */
    @GetMapping("/my-list")
    public Result<List<Info>> getMyNotes(@RequestParam Long userId) {
        QueryWrapper<Info> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        List<Info> list = infoService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 删除笔记
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = infoService.removeById(id);
        if (success) {
            // 👇 同步删除
            noteSearchRepository.deleteById(id);
        }
        return Result.success("删除成功");
    }

    /**
     * 获取笔记详情
     */
    @GetMapping("/detail/{id}")
    public Result<Info> getNoteDetail(@PathVariable Long id) {
        Info info = infoService.getById(id);
        if (info == null) {
            return Result.error("笔记不存在");
        }

        // 👇👇👇 补上这一段：注入作者信息 👇👇👇
        User user = userService.getById(info.getUserId());
        if (user != null) {
            info.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            info.setAuthorAvatar(user.getAvatar());
        }
        // 👆👆👆 结束 👆👆👆

        return Result.success(info);
    }

    /**
     * 🆕 新增接口：发布/保存笔记
     */
    @PostMapping("/save")
    public Result<String> saveNote(@RequestBody Info info) {
        if (info.getTitle() == null || info.getContentHtml() == null) {
            return Result.error("标题和内容不能为空");
        }

        String plainText = HtmlUtil.cleanHtmlTag(info.getContentHtml());
        info.setContentText(plainText);

        if (plainText != null && plainText.length() > 100) {
            info.setSummary(plainText.substring(0, 100) + "...");
        } else {
            info.setSummary(plainText);
        }

        boolean success = infoService.saveOrUpdate(info);

        // 🛑 致命 Bug 修复：这里必须是 status == 2 才能同步到搜索引擎！
        if (success && info.getStatus() == 2) {
            try {
                com.liutong.study.entity.NoteDoc doc = new com.liutong.study.entity.NoteDoc();
                doc.setId(info.getNoteId());
                doc.setTitle(info.getTitle());
                doc.setContent(info.getContentHtml().replaceAll("<[^>]*>", ""));

                // 👇 新增：将分类和标签一起丢进搜索引擎
                doc.setCategoryId(info.getCategoryId());
                doc.setTags(info.getTags());

                doc.setCreateTime(System.currentTimeMillis());

                User user = userService.getById(info.getUserId());
                if (user != null) {
                    doc.setAuthorName(user.getNickname());
                    doc.setAuthorAvatar(user.getAvatar());
                }
                noteSearchRepository.save(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return success ? Result.success("操作成功") : Result.error("操作失败");
    }


    /**
     * 获取公开笔记列表 (默认数据源)
     * 👇 改造点：支持接收 categoryId 参数
     */
    @GetMapping("/list")
    public Result<List<Info>> getAllNotes(@RequestParam(required = false) Long categoryId) {
        QueryWrapper<Info> wrapper = new QueryWrapper<>();

        // 必须是已发布的笔记
        wrapper.eq("status", 2);

        // 👇 改造点：如果前端传了分类ID，就进行过滤
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }

        wrapper.orderByDesc("create_time");

        List<Info> list = infoService.list(wrapper);

        for (Info info : list) {
            User user = userService.getById(info.getUserId());
            if (user != null) {
                info.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                info.setAuthorAvatar(user.getAvatar());
            } else {
                info.setAuthorName("未知极客");
            }
        }
        return Result.success(list);
    }

}