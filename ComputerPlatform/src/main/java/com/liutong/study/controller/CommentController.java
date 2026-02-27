package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.Comment;
import com.liutong.study.entity.User;
import com.liutong.study.service.ICommentService;
import com.liutong.study.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IUserService userService;

    /**
     * 📜 获取某篇笔记的评论列表
     * API: GET /comment/list?noteId=5
     */
    @GetMapping("/list")
    public Result<List<Comment>> getCommentList(@RequestParam Long noteId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("note_id", noteId);
        wrapper.orderByDesc("create_time");
        List<Comment> list = commentService.list(wrapper);

        // 👇👇👇 核心修改：遍历每一条评论，去 User 表查头像和昵称 👇👇👇
        for (Comment comment : list) {
            // 根据评论里的 userId 查用户表
            User user = userService.getById(comment.getUserId());
            if (user != null) {
                // 把查到的昵称和头像填进 Comment 对象里
                comment.setNickname(user.getNickname());
                comment.setAvatar(user.getAvatar());
            } else {
                // 如果用户被删了，给个默认值
                comment.setNickname("未知用户");
            }
        }
        // 👆👆👆 修改结束 👆👆👆

        return Result.success(list);
    }

    /**
     * 🗣️ 发表评论
     * API: POST /comment/save
     */
    @PostMapping("/save")
    public Result<String> saveComment(@RequestBody Comment comment) {
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            return Result.error("评论内容不能为空");
        }
        comment.setCreateTime(LocalDateTime.now());
        boolean success = commentService.save(comment);
        return success ? Result.success("评论成功") : Result.error("评论失败");
    }

    /**
     * 🗑️ 删除评论
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteComment(@PathVariable Long id) {
        return commentService.removeById(id) ? Result.success("删除成功") : Result.error("删除失败");
    }
}