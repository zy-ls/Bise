package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.Info;
import com.liutong.study.entity.Report;
import com.liutong.study.service.IInfoService;
import com.liutong.study.service.IStudyGroupService;
import com.liutong.study.service.IUserService;
import com.liutong.study.mapper.InfoMapper;
import com.liutong.study.mapper.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🛡️ 专门供 Admin 后台管理系统调用的统计接口
 */
@RestController
@RequestMapping("/admin/stats")
public class AdminController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IInfoService infoService;
    @Autowired
    private IStudyGroupService groupService;
    @Autowired
    private InfoMapper infoMapper;

    // 💡 引入举报表的 Mapper
    @Autowired
    private ReportMapper reportMapper;

    /**
     * 📊 获取后台大屏概览数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> data = new HashMap<>();

        // 1. ======== 核心卡片指标 ========
        // 总用户数
        data.put("totalUsers", userService.count());
        // 总学习小组数
        data.put("totalGroups", groupService.count());

        // 已发布(公开)的笔记数 (status = 2)
        QueryWrapper<Info> publishedWrapper = new QueryWrapper<>();
        publishedWrapper.eq("status", 2);
        data.put("totalNotes", infoService.count(publishedWrapper));

        // 🚨 💡 核心修复：待审核的工单数！
        // 以前是查 Info 表，现在改为查 Report 表里的未处理工单 (status = 0)
        QueryWrapper<Report> reportWrapper = new QueryWrapper<>();
        reportWrapper.eq("status", 0);
        data.put("pendingAudits", reportMapper.selectCount(reportWrapper));

        // 2. ======== 趋势折线图数据 ========
        List<Map<String, Object>> trendData = infoMapper.getNoteTrendDays();
        data.put("trendData", trendData);

        return Result.success(data);
    }
}