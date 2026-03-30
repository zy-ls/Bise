package com.liutong.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.util.Date;

@Data
@TableName("sys_report")
public class Report {

    // 💡 修复 1：明确告诉 MyBatis-Plus 使用数据库自增短 ID (1,2,3...)，而不是 19位雪花ID
    @TableId(type = IdType.AUTO)
    // 💡 修复 2：将 Long 类型转为 String 传给前端，彻底杜绝 JS 精度丢失！
    @JsonSerialize(using = ToStringSerializer.class)
    private Long reportId;

    // 嫌疑笔记的 ID 和举报人 ID 同样加上防丢精度的注解
    @JsonSerialize(using = ToStringSerializer.class)
    private Long noteId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long reporterId;

    private String reason;
    private Integer status; // 0-待处理, 1-已处理(封禁笔记), 2-已驳回(误报)
    private Date createTime;
}