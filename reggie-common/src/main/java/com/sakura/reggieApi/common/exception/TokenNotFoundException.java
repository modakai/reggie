package com.sakura.reggieApi.common.exception;

/**
 *  找不到 token 的异常
 * @author sakura
 * @className TokenNotFoundException
 * @createTime 2023/2/9
 */
public class TokenNotFoundException extends RuntimeException{
    public TokenNotFoundException(String message) {
        super(message);
    }
}
