package com.liutong.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liutong.study.entity.SysCategory;
import com.liutong.study.mapper.SysCategoryMapper;
import com.liutong.study.service.ISysCategoryService;
import org.springframework.stereotype.Service;

/**
 * 笔记分类 服务实现类
 */
@Service
public class SysCategoryServiceImpl extends ServiceImpl<SysCategoryMapper, SysCategory> implements ISysCategoryService {
}