package com.liutong.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_banner")
public class Banner {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String imgUrl;
    private String targetUrl;
    private String title;
    private Integer status; // 0-隐藏, 1-显示
    private Integer sortOrder;
}