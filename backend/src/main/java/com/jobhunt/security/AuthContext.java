package com.jobhunt.security;

/**
 * 基于 ThreadLocal 的鉴权上下文配置，用于在当前线程生命周期内存取当前用户的 ID。
 * 从而杜绝水平越权漏洞。
 */
public class AuthContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
    }
}
