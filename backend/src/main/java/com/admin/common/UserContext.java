package com.admin.common;

/**
 * 当前登录用户上下文（基于 ThreadLocal，请求结束后由拦截器清理）
 */
public class UserContext {

    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        CURRENT_USER.set(userId);
    }

    /** 获取当前登录用户 id（未登录返回 null） */
    public static Long getUserId() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
