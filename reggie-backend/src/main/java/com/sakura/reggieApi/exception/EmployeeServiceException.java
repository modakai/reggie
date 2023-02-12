package com.sakura.reggieApi.exception;

/**
 * @author sakura
 * @className EmployeeServiceException
 * @createTime 2023/2/11
 */
public class EmployeeServiceException extends RuntimeException{
    public EmployeeServiceException() {
        super();
    }

    public EmployeeServiceException(String message) {
        super(message);
    }
}
