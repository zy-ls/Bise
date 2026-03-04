package com.liutong.study.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.entity.ChatMessage;
import com.liutong.study.entity.Todo;
import com.liutong.study.service.IChatMessageService;
import com.liutong.study.service.ITodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Component
public class TodoScheduleTask {

    @Autowired
    private ITodoService todoService;

    @Autowired
    private IChatMessageService chatMessageService;

    // cron = "0 * * * * ?" 表示每分钟的第0秒执行一次
    @Scheduled(cron = "0 * * * * ?")
    public void checkTodoDeadlines() {
        System.out.println("⏳ 正在执行待办任务扫描...");

        // 1. 计算 5 分钟后的时间范围 (比如现在是 10:00，那就查 10:05 分的任务)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = now.plusMinutes(5);

        // 格式化为字符串用于数据库比较
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String targetTimeStr = targetTime.format(formatter);

        // 2. 查询条件：未完成的，且截止时间(精确到分钟)刚好是 targetTimeStr 的
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_completed", 0)
                .isNotNull("deadline")
                // 使用 MySQL 的 DATE_FORMAT 截取到分钟进行严格匹配
                .apply("DATE_FORMAT(deadline, '%Y-%m-%d %H:%i') = '" + targetTimeStr + "'");

        List<Todo> urgentTodos = todoService.list(queryWrapper);

        // 3. 遍历这些紧急任务，发送私信通知
        for (Todo todo : urgentTodos) {
            ChatMessage msg = new ChatMessage();
            msg.setSenderId(1L); // 假设 1 号用户是系统管理员/系统助手
            msg.setReceiverId(todo.getUserId()); // 发给设定该待办的用户
            msg.setContent("📢 【系统提醒】您有一个学习计划即将在 5 分钟后到期：\n《" + todo.getContent() + "》\n请抓紧时间完成哦！");
            msg.setIsRead((byte) 0); // 强制转换为 byte 类型
            msg.setCreateTime(LocalDateTime.now()); // 使用 LocalDateTime 获取当前时间

            chatMessageService.save(msg);
            System.out.println("✅ 已向用户 " + todo.getUserId() + " 发送待办到期提醒！");
        }
    }
}