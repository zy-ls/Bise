package com.liutong.study.service.impl;

import com.liutong.study.entity.Version;
import com.liutong.study.mapper.VersionMapper;
import com.liutong.study.service.IVersionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 笔记历史版本表 服务实现类
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Service
public class VersionServiceImpl extends ServiceImpl<VersionMapper, Version> implements IVersionService {

}
