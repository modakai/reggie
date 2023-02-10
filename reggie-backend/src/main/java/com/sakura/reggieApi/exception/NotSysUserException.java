package com.sakura.reggieApi.exception;

/**
 * @author sakura
 * @className NotSysUserException
 * @createTime 2023/2/10
 */
public class NotSysUserException extends RuntimeException{
    public NotSysUserException(String message) {
        super(message);
    }
}
