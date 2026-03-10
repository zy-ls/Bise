package com.liutong.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 笔记分类实体类
 */
@Data
@TableName("sys_category")
public class SysCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @TableId(value = "category_id", type = IdType.AUTO)
    private Long categoryId;

    /**
     * 分类名称 (如: Java, 算法, 前端, 数据库)
     */
    private String name;

    /**
     * 对应的UI图标名 (如: Monitor, DataLine)
     */
    private String icon;

    /**
     * 排序权重
     */
    private Integer sort;
}