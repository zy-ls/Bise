package com.liutong.study.service.impl;

import com.liutong.study.entity.ChatMessage;
import com.liutong.study.mapper.ChatMessageMapper;
import com.liutong.study.service.IChatMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 私信消息表 服务实现类
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatMessageService {

}
