package com.liutong.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Literature {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String author; // 文献的原作者
    private String summary;
    private String filePath; // 核心：存 D:/upload/xxx.pdf
    private String fileSize;
    private LocalDateTime uploadTime;
    private Long uploaderId; // 谁传的
    private Integer status;
}