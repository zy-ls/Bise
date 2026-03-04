package com.liutong.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liutong.study.entity.Todo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TodoMapper extends BaseMapper<Todo> {
}