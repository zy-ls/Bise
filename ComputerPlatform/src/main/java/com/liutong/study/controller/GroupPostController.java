package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.GroupPost;
import com.liutong.study.entity.User;
import com.liutong.study.service.IGroupPostService;
import com.liutong.study.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/group-post")
public class GroupPostController {

    @Autowired
    private IGroupPostService groupPostService;
    @Autowired
    private IUserService userService;

    @GetMapping("/list")
    public Result<List<GroupPost>> getList(@RequestParam Long groupId) {
        List<GroupPost> list = groupPostService.list(
                new LambdaQueryWrapper<GroupPost>()
                        .eq(GroupPost::getGroupId, groupId)
                        .orderByDesc(GroupPost::getCreateTime)
        );
        // 组装发布人的头像和昵称
        for (GroupPost post : list) {
            User user = userService.getById(post.getUserId());
            if (user != null) {
                post.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                post.setAuthorAvatar(user.getAvatar());
            } else {
                post.setAuthorName("匿名用户");
            }
        }
        return Result.success(list);
    }

    @PostMapping("/publish")
    public Result<String> publish(@RequestBody GroupPost post) {
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            return Result.error("发布内容不能为空");
        }
        post.setCreateTime(new Date());
        boolean success = groupPostService.save(post);
        return success ? Result.success("发布成功") : Result.error("发布失败");
    }
}