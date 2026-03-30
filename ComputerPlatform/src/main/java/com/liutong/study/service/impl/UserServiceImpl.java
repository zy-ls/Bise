package com.liutong.study.service.impl;

import com.liutong.study.entity.User;
import com.liutong.study.mapper.UserMapper;
import com.liutong.study.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Override
    public List<Map<String, Object>> getActivityStats(Long userId) {
        return baseMapper.getUserActivityStats(userId);
    }

    @Override
    public List<Map<String, Object>> getRadarStats(Long userId) {
        return baseMapper.getUserRadarStats(userId);
    }

}
