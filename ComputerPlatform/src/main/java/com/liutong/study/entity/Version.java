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
 * 笔记历史版本表
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Getter
@Setter
@TableName("note_version")
@ApiModel(value = "Version对象", description = "笔记历史版本表")
public class Version implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "version_id", type = IdType.AUTO)
    private Long versionId;

    @ApiModelProperty("关联笔记ID")
    @TableField("note_id")
    private Long noteId;

    @ApiModelProperty("版本号(1,2,3...)")
    @TableField("version_number")
    private Integer versionNumber;

    @ApiModelProperty("内容快照")
    @TableField("content_snapshot")
    private String contentSnapshot;

    @ApiModelProperty("备份时间")
    @TableField("create_time")
    private LocalDateTime createTime;
}
