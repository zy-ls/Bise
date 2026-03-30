package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.Info;
import com.liutong.study.entity.Report;
import com.liutong.study.mapper.ReportMapper;
import com.liutong.study.service.IInfoService;
import com.liutong.study.repository.NoteSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 🚨 用户举报工单处理
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private IInfoService infoService;
    @Autowired
    private NoteSearchRepository noteSearchRepository;

    /**
     * 前台：用户提交举报
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Report report) {
        report.setStatus(0); // 默认为待处理
        reportMapper.insert(report);
        return Result.success("举报已提交，感谢您为社区环境作出的贡献！");
    }

    /**
     * 后台：管理员获取所有待处理举报
     */
    @GetMapping("/list")
    public Result<List<Report>> list() {
        QueryWrapper<Report> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("status"); // 待处理(0)排在前面
        wrapper.orderByDesc("create_time");
        return Result.success(reportMapper.selectList(wrapper));
    }

    /**
     * 后台：管理员处理举报 (决定封禁还是驳回)
     */
    @PostMapping("/process")
    public Result<String> process(@RequestParam Long reportId, @RequestParam Integer status, @RequestParam Long noteId) {
        Report report = reportMapper.selectById(reportId);
        if (report != null) {
            report.setStatus(status);
            reportMapper.updateById(report);

            // 💡 如果管理员判定举报有效，选择封禁该笔记 (status = 1)
            if (status == 1) {
                Info note = infoService.getById(noteId);
                if (note != null) {
                    note.setStatus((byte) 3); // 3代表违规下架
                    infoService.updateById(note);
                    // 并且将这篇污点文章从 OpenSearch 广场中抹除！
                    noteSearchRepository.deleteById(noteId);
                }
            }
            return Result.success("工单处理完成！");
        }
        return Result.error("找不到该工单");
    }
}