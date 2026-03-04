package com.liutong.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liutong.study.entity.Todo;
import com.liutong.study.mapper.TodoMapper;
import com.liutong.study.service.ITodoService;
import org.springframework.stereotype.Service;

@Service
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo> implements ITodoService {
}