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
 * 学习资料表
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Getter
@Setter
@TableName("sys_resource")
@ApiModel(value = "Resource对象", description = "学习资料表")
public class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "resource_id", type = IdType.AUTO)
    private Long resourceId;

    @ApiModelProperty("上传者ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("文件名")
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty("文件存储路径/URL")
    @TableField("file_url")
    private String fileUrl;

    @ApiModelProperty("文件类型(pdf, ppt, doc)")
    @TableField("file_type")
    private String fileType;

    @ApiModelProperty("文件大小(字节)")
    @TableField("file_size")
    private Long fileSize;

    @ApiModelProperty("资源描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("下载次数")
    @TableField("download_count")
    private Integer downloadCount;

    @TableField("create_time")
    private LocalDateTime createTime;
}
