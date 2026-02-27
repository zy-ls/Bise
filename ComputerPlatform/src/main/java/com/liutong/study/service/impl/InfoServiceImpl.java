package com.liutong.study.service.impl;

import com.liutong.study.entity.Info;
import com.liutong.study.mapper.InfoMapper;
import com.liutong.study.service.IInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 笔记信息主表 服务实现类
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Service
public class InfoServiceImpl extends ServiceImpl<InfoMapper, Info> implements IInfoService {

}
