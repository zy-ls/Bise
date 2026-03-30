package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.Announcement;
import com.liutong.study.mapper.AnnouncementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @GetMapping("/list")
    public Result<List<Announcement>> list(@RequestParam(required = false) Integer status) {
        QueryWrapper<Announcement> wrapper = new QueryWrapper<>();
        if (status != null) {
            wrapper.eq("status", status); // 前台只查发布的
        }
        wrapper.orderByDesc("create_time"); // 最新的在最上面
        return Result.success(announcementMapper.selectList(wrapper));
    }

    @PostMapping("/save")
    public Result<String> save(@RequestBody Announcement ann) {
        if (ann.getId() == null) {
            announcementMapper.insert(ann);
        } else {
            announcementMapper.updateById(ann);
        }
        return Result.success("公告发布成功！");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        announcementMapper.deleteById(id);
        return Result.success("删除成功");
    }
}