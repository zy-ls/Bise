package com.liutong.study.service.impl;

import com.liutong.study.entity.Resource;
import com.liutong.study.mapper.ResourceMapper;
import com.liutong.study.service.IResourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学习资料表 服务实现类
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements IResourceService {

}
