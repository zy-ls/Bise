package com.liutong.study.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.Resource; // 确保引用你刚才发的这个类
import com.liutong.study.service.IResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 资料/资源控制器 (修正版)
 */
@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private IResourceService resourceService;

    // 📥 确保这个路径存在，或者改成你自己的
    private static final String UPLOAD_PATH = "D:/ComputerPlatform/upload/";

    /**
     * 获取资料列表
     */
    @GetMapping("/list")
    public Result<List<Resource>> getList() {
        QueryWrapper<Resource> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return Result.success(resourceService.list(wrapper));
    }

    /**
     * 📤 文件上传接口
     */
    @PostMapping("/upload")
    public Result<Resource> upload(@RequestParam MultipartFile file, @RequestParam Long userId) throws IOException {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 1. 获取文件信息
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename); // pdf, docx
        long size = file.getSize(); // 文件大小(字节)

        // 2. 保存到硬盘
        String fileUuid = IdUtil.simpleUUID() + "." + suffix;
        File saveFile = new File(UPLOAD_PATH + fileUuid);
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        file.transferTo(saveFile);

        // 3. 保存到数据库 (严格匹配你的 Resource.java)
        Resource resource = new Resource();
        resource.setUserId(userId);

        // 修正点1：你用的是 fileName，不是 title
        resource.setFileName(originalFilename);

        // 修正点2：你用的是 fileUrl，不是 url
        resource.setFileUrl(fileUuid);

        resource.setFileType(suffix);

        // 修正点3：你的 fileSize 是 Long 类型，直接存字节数，不要转成 "MB" 字符串
        resource.setFileSize(size);

        // 初始化下载次数为 0
        resource.setDownloadCount(0);

        // 修正点4：你的类里没有 status 字段，所以我删掉了 setStatus(1)

        resourceService.save(resource);

        return Result.success(resource);
    }

    /**
     * 📥 文件下载接口
     */
    @GetMapping("/download/{fileUuid}")
    public void download(@PathVariable String fileUuid, HttpServletResponse response) throws IOException {
        File file = new File(UPLOAD_PATH + fileUuid);
        if (!file.exists()) {
            return;
        }

        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileUuid);

        FileUtil.writeToStream(file, response.getOutputStream());

        // (可选) 这里可以顺便加一行代码：更新数据库的 downloadCount + 1
    }
}