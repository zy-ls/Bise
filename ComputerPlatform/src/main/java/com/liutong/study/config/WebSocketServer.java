package com.liutong.study.config;

import org.springframework.stereotype.Component;

// 💡 核心修复：把所有的 javax 换成了 jakarta
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ⚡ WebSocket 全双工通信核心枢纽 (适配 Spring Boot 3.x)
 */
@ServerEndpoint("/ws/chat/{userId}")
@Component
public class WebSocketServer {

    // 存放所有在线客户端的会话池 (线程安全)
    private static final ConcurrentHashMap<Long, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        sessionMap.put(userId, session);
        System.out.println("⚡ [WebSocket] Node Connected: UID=" + userId + ", 当前在线节点数: " + sessionMap.size());
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        sessionMap.remove(userId);
        System.out.println("🔌 [WebSocket] Node Disconnected: UID=" + userId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        // 如果异常是 Tomcat 关机或前端直接关网页导致的“通道关闭”，则静默处理，不打印恐怖的报错堆栈
        String errorName = error.getClass().getSimpleName();
        if ("ClosedChannelException".equals(errorName) || "EOFException".equals(errorName)) {
            System.out.println("⚠️ [WebSocket] 检测到物理断开 (服务器关闭或浏览器直接退出)");
        } else {
            // 其他真正的未知代码错误，才打印出来
            System.out.println("❌ [WebSocket] 真正的链路异常！");
            error.printStackTrace();
        }
    }

    /**
     * 核心方法：服务器主动向指定的 UID 拔打专线并推送消息
     */
    public static void sendMessageToUser(Long userId, String message) {
        Session session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}