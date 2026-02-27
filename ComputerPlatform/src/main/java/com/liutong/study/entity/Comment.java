package com.liutong.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 评论交流表
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Getter
@Setter
@TableName("comment")
@ApiModel(value = "Comment对象", description = "评论交流表")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;

    @ApiModelProperty("关联笔记ID")
    @TableField("note_id")
    private Long noteId;

    @ApiModelProperty("评论者ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("父评论ID(回复某人)")
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty("评论内容")
    @TableField("content")
    private String content;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField(exist = false) // 告诉 MyBatis 这不是数据库字段
    private String nickname;

    @TableField(exist = false)
    private String avatar;
}
