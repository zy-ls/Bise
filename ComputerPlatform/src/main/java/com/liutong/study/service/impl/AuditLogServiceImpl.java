package com.liutong.study.service.impl;

import com.liutong.study.entity.AuditLog;
import com.liutong.study.mapper.AuditLogMapper;
import com.liutong.study.service.IAuditLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 内容审核记录表 服务实现类
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog> implements IAuditLogService {

}
