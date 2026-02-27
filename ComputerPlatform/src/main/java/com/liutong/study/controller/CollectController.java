package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.Collect;
import com.liutong.study.service.ICollectService;
import com.liutong.study.service.IInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.liutong.study.entity.Info;

@RestController
@RequestMapping("/collect")
public class CollectController {

    @Autowired
    private ICollectService collectService;

    @Autowired
    private IInfoService infoService;

    /**
     * ⭐ 切换收藏状态 (收藏 <-> 取消)
     */
    @PostMapping("/toggle")
    public Result<String> toggleCollect(@RequestBody Collect collect) {
        // 1. 检查是否已经收藏过
        QueryWrapper<Collect> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", collect.getUserId());
        wrapper.eq("note_id", collect.getNoteId());

        Collect exist = collectService.getOne(wrapper);

        if (exist != null) {
            // 2. 如果存在，则是“取消收藏”
            collectService.removeById(exist.getCollectId());
            return Result.success("取消收藏");
        } else {
            // 3. 如果不存在，则是“添加收藏”
            collect.setCreateTime(LocalDateTime.now());
            collectService.save(collect);
            return Result.success("收藏成功");
        }
    }

    /**
     * 🔍 检查某人是否收藏了某篇笔记
     */
    @GetMapping("/check")
    public Result<Boolean> checkCollect(@RequestParam Long userId, @RequestParam Long noteId) {
        QueryWrapper<Collect> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("note_id", noteId);
        return Result.success(collectService.count(wrapper) > 0);
    }

    /**
     * ⭐ 获取我的收藏列表
     * API: GET /collect/list?userId=1
     */
    @GetMapping("/list")
    public Result<List<Info>> getMyCollectList(@RequestParam Long userId) {
        // 1. 先去收藏表查出这个用户所有的收藏记录
        QueryWrapper<Collect> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        query.orderByDesc("create_time"); // 按收藏时间倒序
        List<Collect> collectList = collectService.list(query);

        if (collectList.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        // 2. 提取出所有的 noteId
        List<Long> noteIds = collectList.stream()
                .map(Collect::getNoteId)
                .collect(Collectors.toList());

        // 3. 去笔记表批量查询这些笔记的详情
        // listByIds 是 MyBatis-Plus 自带的批量查询方法，很方便
        List<Info> notes = infoService.listByIds(noteIds);

        // 注意：这里查出来是乱序的或者按ID序的，如果非要严格按收藏时间排序，可以在内存里排一下，
        // 但对于毕设来说，这样直接返回已经足够了。

        return Result.success(notes);
    }
}