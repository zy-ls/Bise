package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.ChatMessage;
import com.liutong.study.entity.User;
import com.liutong.study.service.IChatMessageService;
import com.liutong.study.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 聊天消息控制器
 */
@RestController
@RequestMapping("/chat")
public class  ChatMessageController {

    @Autowired
    private IChatMessageService chatMessageService;

    @Autowired
    private IUserService userService;

    /**
     * 👥 获取“最近联系人”列表
     * 逻辑：查出所有和我有关的消息，提取对方的ID，去重
     */
    @GetMapping("/contacts")
    public Result<List<User>> getContacts(@RequestParam Long userId) {
        // 1. 查出所有我发出的、或者发给我的消息
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("sender_id", userId).or().eq("receiver_id", userId);
        wrapper.orderByDesc("create_time"); // 按时间倒序
        List<ChatMessage> msgs = chatMessageService.list(wrapper);

        // 2. 提取对方的 ID (使用 Set 自动去重)
        Set<Long> contactIds = new HashSet<>();
        for (ChatMessage msg : msgs) {
            if (msg.getSenderId().equals(userId)) {
                // 如果我是发送者，对方就是接收者
                contactIds.add(msg.getReceiverId());
            } else {
                // 如果我是接收者，对方就是发送者
                contactIds.add(msg.getSenderId());
            }
        }

        // 3. 如果没有任何聊天记录，返回空列表
        if (contactIds.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        // 4. 根据 ID 集合去数据库查用户详情
        List<User> users = userService.listByIds(contactIds);
        return Result.success(users);
    }

    /**
     * 📜 获取我和某人的聊天记录
     * API: GET /chat/history?senderId=1&receiverId=2
     */
    @GetMapping("/history")
    public Result<List<ChatMessage>> getHistory(@RequestParam Long senderId, @RequestParam Long receiverId) {
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();

        // 逻辑：(我是发送者 且 他是接收者) 或者 (他是发送者 且 我是接收者)
        // 也就是：查我不论是“发”还是“收”的所有关于他的消息
        wrapper.and(wp -> wp
                .eq("sender_id", senderId).eq("receiver_id", receiverId)
                .or()
                .eq("sender_id", receiverId).eq("receiver_id", senderId)
        );

        wrapper.orderByAsc("create_time"); // 按时间顺序排列，旧的在上面

        return Result.success(chatMessageService.list(wrapper));
    }

    /**
     * 📨 发送消息
     * API: POST /chat/send
     */
    @PostMapping("/send")
    public Result<String> sendMessage(@RequestBody ChatMessage message) {
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return Result.error("内容不能为空");
        }
        message.setCreateTime(LocalDateTime.now());
        message.setIsRead((byte) 0);
        boolean success = chatMessageService.save(message);
        return success ? Result.success("发送成功") : Result.error("发送失败");
    }
}