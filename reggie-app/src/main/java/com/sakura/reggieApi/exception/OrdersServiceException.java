package com.sakura.reggieApi.exception;

/**
 * @author sakura
 * @className OrdersServiceException
 * @createTime 2023/2/15
 */
public class OrdersServiceException extends RuntimeException{
    public OrdersServiceException(String message) {
        super(message);
    }
}
