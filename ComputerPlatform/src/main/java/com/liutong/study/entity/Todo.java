package com.liutong.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat; // 👈 新增导入这个包
import lombok.Data;
import java.util.Date;

@Data
@TableName("sys_todo")
public class Todo {
    @TableId(type = IdType.AUTO)
    private Long todoId;

    private Long userId;

    private String content;

    private Integer isCompleted;

    // 👈 核心修复：明确指定前后端交互的时间格式和时区
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deadline;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}