package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liutong.study.entity.SysCategory;
import com.liutong.study.service.ISysCategoryService;
import com.liutong.study.common.Result; // 替换成你自己的返回结果类
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/sys-category")
public class SysCategoryController {

    @Autowired
    private ISysCategoryService sysCategoryService;

    @GetMapping("/list")
    public Result getCategoryList() {
        // 按照 sort_order 降序排列 (数字越大越靠前)
        List<SysCategory> list = sysCategoryService.list(
                new LambdaQueryWrapper<SysCategory>().orderByDesc(SysCategory::getSort)
        );
        return Result.success(list);
    }
}