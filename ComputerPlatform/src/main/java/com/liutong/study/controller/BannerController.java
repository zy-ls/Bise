package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.Banner;
import com.liutong.study.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banner")
public class BannerController {

    @Autowired
    private BannerMapper bannerMapper;

    /**
     * 获取轮播图列表 (B端查全部，C端传 status=1 只查启用的)
     */
    @GetMapping("/list")
    public Result<List<Banner>> list(@RequestParam(required = false) Integer status) {
        QueryWrapper<Banner> wrapper = new QueryWrapper<>();
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByAsc("sort_order").orderByDesc("create_time");
        return Result.success(bannerMapper.selectList(wrapper));
    }

    /**
     * 新增或修改轮播图
     */
    @PostMapping("/save")
    public Result<String> save(@RequestBody Banner banner) {
        if (banner.getId() == null) {
            bannerMapper.insert(banner);
        } else {
            bannerMapper.updateById(banner);
        }
        return Result.success("推荐位配置保存成功！");
    }

    /**
     * 删除轮播图
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        bannerMapper.deleteById(id);
        return Result.success("删除成功");
    }
}