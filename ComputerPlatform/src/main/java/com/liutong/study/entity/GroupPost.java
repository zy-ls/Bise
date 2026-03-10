package com.liutong.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("group_post")
public class GroupPost {
    @TableId(value = "post_id", type = IdType.AUTO)
    private Long postId;
    private Long groupId;
    private Long userId;
    private String content;
    private Date createTime;

    // 以下两个字段不在数据库中，仅用于给前端返回作者信息
    @TableField(exist = false)
    private String authorName;
    @TableField(exist = false)
    private String authorAvatar;
}