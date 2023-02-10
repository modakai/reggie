package com.sakura.reggieApi.exception;

/**
 * @author sakura
 * @className EmployeeUsernameRepeatException
 * @createTime 2023/2/10
 */
public class EmployeeUsernameRepeatException extends RuntimeException{
    public EmployeeUsernameRepeatException() {
        super();
    }

    public EmployeeUsernameRepeatException(String message) {
        super(message);
    }
}
