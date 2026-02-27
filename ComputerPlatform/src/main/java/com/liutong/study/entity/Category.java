package com.liutong.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 笔记分类表
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Getter
@Setter
@TableName("note_category")
@ApiModel(value = "Category对象", description = "笔记分类表")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "category_id", type = IdType.AUTO)
    private Long categoryId;

    @ApiModelProperty("所属用户ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("分类名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("父分类ID (0表示根目录)")
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty("排序权重")
    @TableField("sort_order")
    private Integer sortOrder;
}
