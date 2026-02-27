package com.liutong.study.service.impl;

import com.liutong.study.entity.Category;
import com.liutong.study.mapper.CategoryMapper;
import com.liutong.study.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 笔记分类表 服务实现类
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
