package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.AuditLog;
import com.liutong.study.service.IAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 内容审核记录表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/auditLog")
public class AuditLogController {

    @Autowired
    private IAuditLogService auditLogService;

    /**
     * 获取历史审核记录列表 (供 Admin 控制台审计日志调用)
     */
    @GetMapping("/list")
    public Result<List<AuditLog>> list() {
        QueryWrapper<AuditLog> wrapper = new QueryWrapper<>();
        // 按审核时间倒序排列，最新的在最上面
        wrapper.orderByDesc("audit_time");
        return Result.success(auditLogService.list(wrapper));
    }
}