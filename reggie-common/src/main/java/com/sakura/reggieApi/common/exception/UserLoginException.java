package com.sakura.reggieApi.common.exception;

/**
 * @author sakura
 * @className UserLoginException
 * @createTime 2023/2/9
 */
public class UserLoginException extends RuntimeException{
    public UserLoginException(String message) {
        super(message);
    }
}
