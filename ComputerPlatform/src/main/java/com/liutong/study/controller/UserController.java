package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.User;
import com.liutong.study.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/sys-user") // 注意：CodeGenerator 生成的路径可能是 /sys-user
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 登录接口 (保留之前的)
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        wrapper.eq("password", user.getPassword());
        User loginUser = userService.getOne(wrapper);
        if (loginUser != null) {
            return Result.success(loginUser);
        }
        return Result.error("账号或密码错误");
    }

    /**
     * 🆕 更新个人信息 (昵称、邮箱、头像)
     */
    @PostMapping("/update")
    public Result<User> updateInfo(@RequestBody User user) {
        // 出于安全，防止用户恶意篡改用户名或密码，这里强制设为null(MyBatisPlus更新时会忽略null字段)
        // 如果你想允许改密码，需要单独写改密码接口
        user.setUsername(null);
        user.setPassword(null);

        boolean success = userService.updateById(user);
        if (success) {
            // 更新成功后，查出最新的数据返回给前端更新缓存
            return Result.success(userService.getById(user.getUserId()));
        }
        return Result.error("修改失败");
    }

    /**
     * 🆕 获取当前用户信息
     */
    @GetMapping("/info/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }
}