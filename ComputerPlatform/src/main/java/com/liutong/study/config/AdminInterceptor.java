package com.liutong.study.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liutong.study.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 🛡️ 后端安全防线：拦截所有对 Admin 接口的请求
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 💡 核心修复：直接放行浏览器的 CORS 预检请求 (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头中获取前端传来的角色信息
        String userRole = request.getHeader("X-User-Role");

        // 如果是管理员，直接放行
        if ("ADMIN".equals(userRole)) {
            return true;
        }

        // 🚨 权限不足，直接在后端把请求打回，返回 403 错误 JSON
        response.setContentType("application/json;charset=utf-8");

        Result<String> errorResult = Result.error("403 Forbidden: 拒绝访问，非管理员越权操作！");
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResult));

        return false; // 拦截请求，不再往下执行
    }
}