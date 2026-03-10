package com.liutong.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liutong.study.entity.SysCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 笔记分类 Mapper 接口
 */
@Mapper
public interface SysCategoryMapper extends BaseMapper<SysCategory> {
}