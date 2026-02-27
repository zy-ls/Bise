package com.liutong.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_collect")
public class Collect {
    @TableId(value = "collect_id", type = IdType.AUTO)
    private Long collectId;
    private Long userId;
    private Long noteId;
    private LocalDateTime createTime;
}