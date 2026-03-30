package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.User;
import com.liutong.study.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/sys-user") // 注意这里的路径是 sys-user
public class UserController {

    @Autowired
    private IUserService userService;

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

    @PostMapping("/update")
    public Result<User> updateInfo(@RequestBody User user) {
        user.setUsername(null);
        user.setPassword(null);
        boolean success = userService.updateById(user);
        if (success) {
            return Result.success(userService.getById(user.getUserId()));
        }
        return Result.error("修改失败");
    }

    @GetMapping("/info/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    // ================== 下面是补上的两个统计大屏图表接口 ==================

    /**
     * 获取用户 365 天活跃度贡献图数据
     */
    @GetMapping("/stats/activity")
    public Result<List<Map<String, Object>>> getActivityStats(@RequestParam Long userId) {
        return Result.success(userService.getActivityStats(userId));
    }

    /**
     * 获取用户能力雷达图数据 (附带数据转化算法)
     */
    @GetMapping("/stats/radar")
    public Result<List<Map<String, Object>>> getRadarStats(@RequestParam Long userId) {
        List<Map<String, Object>> rawStats = userService.getRadarStats(userId);

        // 极客算法：将普通的“发帖数量”转化为 100分制的“能力评分”
        for (Map<String, Object> stat : rawStats) {
            // 获取数据库返回的 count
            long count = ((Number) stat.get("count")).longValue();
            // 评分公式：基础分 20 + 每发一篇笔记加 15 分，最高不超过 100 分
            long score = Math.min(100L, 20L + (count * 15L));
            stat.put("value", score);
        }
        return Result.success(rawStats);
    }
}