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
 * 笔记信息主表
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Getter
@Setter
@TableName("note_info")
@ApiModel(value = "Info对象", description = "笔记信息主表")
public class Info implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "note_id", type = IdType.AUTO)
    private Long noteId;

    @ApiModelProperty("作者ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("分类ID")
    @TableField("category_id")
    private Long categoryId;

    @ApiModelProperty("笔记标题")
    @TableField("title")
    private String title;

    @ApiModelProperty("摘要(用于搜索列表展示)")
    @TableField("summary")
    private String summary;

    @ApiModelProperty("富文本内容(带HTML标签)")
    @TableField("content_html")
    private String contentHtml;

    @ApiModelProperty("纯文本内容(用于OpenSearch分词索引)")
    @TableField("content_text")
    private String contentText;

    @ApiModelProperty("状态: 0-私有, 1-待审核, 2-已发布(公开), 3-驳回")
    @TableField("status")
    private Byte status;

    @ApiModelProperty("浏览量")
    @TableField("view_count")
    private Integer viewCount;

    @ApiModelProperty("点赞数")
    @TableField("like_count")
    private Integer likeCount;

    @ApiModelProperty("收藏数")
    @TableField("collect_count")
    private Integer collectCount;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;


    @TableField(exist = false) // 数据库里没有，只用于前端显示
    private String authorName;

    @TableField(exist = false)
    private String authorAvatar;

}
