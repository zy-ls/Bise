package com.liutong.study.controller;

import com.liutong.study.common.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    // 🔴 必改：请确保这个文件夹在你的电脑上存在！
    private static final String UPLOAD_ROOT = "D:/ComputerPlatform/literature/";

    /**
     * 📤 文件上传接口
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        try {
            // 1. 获取原文件名
            String originalFilename = file.getOriginalFilename();
            // 2. 生成一个唯一文件名，防止重名覆盖 (例如: uuid_原名.pdf)
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String newFileName = uuid + "_" + originalFilename;

            // 3. 创建文件对象
            File saveFile = new File(UPLOAD_ROOT + newFileName);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }

            // 4. 保存文件到磁盘
            file.transferTo(saveFile);

            log.info("文件上传成功: {}", saveFile.getAbsolutePath());

            // 5. 返回文件的绝对路径给前端（前端拿到后，填入 note_info 表的 file_path 字段）
            return Result.success(saveFile.getAbsolutePath());

        } catch (IOException e) {
            log.error("上传失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    /**
     * 📄 在线预览/下载接口
     * 访问地址示例: http://localhost:8080/file/preview?path=D:/project_upload/xxx.pdf
     */
    @GetMapping("/preview")
    public void preview(@RequestParam String path, HttpServletResponse response) {
        // 安全检查：防止读取非上传目录的文件
//        if (!path.startsWith(UPLOAD_ROOT)) {
//            return;
//        }

        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        // 1. 设置响应头，告诉浏览器这是 PDF
        response.reset();
        response.setContentType("application/pdf");
        // inline 表示在浏览器内打开，如果想强制下载改为 attachment
        response.setHeader("Content-Disposition", "inline; filename=file.pdf");

        // 2. 读取文件流并写回浏览器
        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[1024 * 8]; // 8KB buffer
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            log.error("文件读取失败", e);
        }
    }
}