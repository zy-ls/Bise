package com.liutong.study.controller;

import com.liutong.study.common.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    // 存放文件的根目录
    private static final String UPLOAD_ROOT = "D:/ComputerPlatform/literature/";

    /**
     * 📤 原有的文件上传接口 (保留给文献PDF等使用)
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        try {
            String originalFilename = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String newFileName = uuid + "_" + originalFilename;

            File saveFile = new File(UPLOAD_ROOT + newFileName);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            file.transferTo(saveFile);
            log.info("文件上传成功: {}", saveFile.getAbsolutePath());
            return Result.success(saveFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("上传失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    /**
     * 📄 原有的在线预览/下载接口 (保留给文献PDF使用)
     */
    @GetMapping("/preview")
    public void preview(@RequestParam String path, HttpServletResponse response) {
        File file = new File(path);
        if (!file.exists()) return;

        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=file.pdf");

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[1024 * 8];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            log.error("文件读取失败", e);
        }
    }

    // ================== 💡 下面是新增的专属图片图床接口 ==================

    /**
     * 🖼️ 专用于头像/图片的上传接口
     */
    @PostMapping("/uploadImage")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return Result.error("上传图片不能为空");
        try {
            String originalFilename = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String newFileName = uuid + "_" + originalFilename;

            File saveFile = new File(UPLOAD_ROOT + newFileName);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            file.transferTo(saveFile);

            // 💡 核心改变：不返回 D盘 路径，而是拼接成一个可以通过浏览器访问的网络 URL！
            String imageUrl = "http://localhost:8080/file/image?name=" + newFileName;
            return Result.success(imageUrl);

        } catch (IOException e) {
            log.error("图片上传失败", e);
            return Result.error("上传图片失败: " + e.getMessage());
        }
    }

    /**
     * 🖼️ 将本地图片转为网络流的接口
     */
    @GetMapping("/image")
    public void getImage(@RequestParam String name, HttpServletResponse response) {
        File file = new File(UPLOAD_ROOT + name);
        if (!file.exists()) return;

        // 告诉浏览器这是一张图片
        response.setContentType("image/jpeg");

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[1024 * 8];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            log.error("图片读取失败", e);
        }
    }
}