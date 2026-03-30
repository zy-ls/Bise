package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.Literature;
import com.liutong.study.mapper.LiteratureMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/literature")
public class LiteratureController {

    @Autowired
    private LiteratureMapper literatureMapper;

    @GetMapping("/list")
    public Result<List<Literature>> list(@RequestParam(required = false) Integer status) {
        QueryWrapper<Literature> wrapper = new QueryWrapper<>();
        if (status != null) {
            wrapper.eq("status", status); // 前台只查上架的
        }
        wrapper.orderByDesc("create_time");
        return Result.success(literatureMapper.selectList(wrapper));
    }

    @PostMapping("/save")
    public Result<String> save(@RequestBody Literature lit) {
        if (lit.getId() == null) {
            literatureMapper.insert(lit);
        } else {
            literatureMapper.updateById(lit);
        }
        return Result.success("文献配置保存成功！");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        literatureMapper.deleteById(id);
        return Result.success("删除成功");
    }
}