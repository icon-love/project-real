package com.admin.common;

import lombok.Getter;

/**
 * 业务异常：抛出后由全局异常处理器转换为统一响应
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.ERROR.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
