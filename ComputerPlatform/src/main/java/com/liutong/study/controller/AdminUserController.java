package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.User;
import com.liutong.study.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private IUserService userService;

    /**
     * 1. 检索用户 (适配你的 user_id 字段)
     */
    @GetMapping("/list")
    public Result<List<User>> list(@RequestParam(required = false) String keyword) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 💡 修正：根据你的表结构，匹配 nickname 和 user_id
            wrapper.like("nickname", keyword).or().eq("user_id", keyword);
        }
        wrapper.orderByDesc("create_time");
        return Result.success(userService.list(wrapper));
    }

    /**
     * 2. 账号封禁与解封 (适配你的 setUserId)
     */
    @PostMapping("/status")
    public Result<String> updateStatus(@RequestParam Long userId, @RequestParam Integer status) {
        User user = new User();
        user.setUserId(userId); // 💡 修正：使用你的 setUserId
        user.setStatus(status);
        userService.updateById(user);
        return Result.success(status == 1 ? "账号已解封！" : "Target Terminated: 账号已永久封禁！");
    }

    /**
     * 3. 提权与降级 (适配你的 STUDENT 角色)
     */
    @PostMapping("/role")
    public Result<String> updateRole(@RequestParam Long userId, @RequestParam String role) {
        User user = new User();
        user.setUserId(userId); // 💡 修正：使用你的 setUserId
        user.setRole(role);
        userService.updateById(user);
        return Result.success("ROOT_OVERRIDE: 用户权限变更已生效！");
    }
}