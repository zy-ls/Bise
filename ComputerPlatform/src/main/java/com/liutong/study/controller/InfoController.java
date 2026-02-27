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
     * API: POST /note-info/save
     */
    @PostMapping("/save")
    public Result<String> saveNote(@RequestBody Info info) {
        // 1. 简单的参数校验
        if (info.getTitle() == null || info.getContentHtml() == null) {
            return Result.error("标题和内容不能为空");
        }

        // 2. 【关键一步】从 HTML 中提取纯文本
        // 为什么要做这步？为了以后 OpenSearch 搜索做准备！
        String plainText = HtmlUtil.cleanHtmlTag(info.getContentHtml());
        info.setContentText(plainText);

        // 3. 自动生成摘要 (取前100字)
        if (plainText != null && plainText.length() > 100) {
            info.setSummary(plainText.substring(0, 100) + "...");
        } else {
            info.setSummary(plainText);
        }

        // 4. 保存到数据库
        // saveOrUpdate: 如果 info 里有 ID 就更新，没 ID 就新增
        boolean success = infoService.saveOrUpdate(info);

        // 👇👇👇 新增同步逻辑 👇👇👇
        if (success && info.getStatus() == 1) {
            try {
                com.liutong.study.entity.NoteDoc doc = new com.liutong.study.entity.NoteDoc();
                doc.setId(info.getNoteId());
                doc.setTitle(info.getTitle());
                // 去除 HTML 标签
                doc.setContent(info.getContentHtml().replaceAll("<[^>]*>", ""));
                doc.setCreateTime(System.currentTimeMillis());

                User user = userService.getById(info.getUserId());
                if (user != null) {
                    doc.setAuthorName(user.getNickname());
                    doc.setAuthorAvatar(user.getAvatar());
                }
                noteSearchRepository.save(doc); // 保存到 OpenSearch
            } catch (Exception e) {
                e.printStackTrace(); // 搜索挂了别影响发笔记
            }
        }
        // 👆👆👆 结束 👆👆👆

        return success ? Result.success("发布成功") : Result.error("发布失败");
    }


    @GetMapping("/list")
    public Result<List<Info>> getAllNotes() {
        QueryWrapper<Info> wrapper = new QueryWrapper<>();

        wrapper.eq("status", 2);

        wrapper.orderByDesc("create_time");

        List<Info> list = infoService.list(wrapper);

        // 填充作者信息
        for (Info info : list) {
            User user = userService.getById(info.getUserId());
            if (user != null) {
                // 使用我们在第一步加的临时字段
                info.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                info.setAuthorAvatar(user.getAvatar());
            } else {
                info.setAuthorName("未知用户");
            }

            // 为了列表页显示美观，把很长的 HTML 内容截断一下，或者只给个摘要
            // 这里我们偷个懒，前端来控制显示长度，后端照常返回
        }
        return Result.success(list);
    }

}