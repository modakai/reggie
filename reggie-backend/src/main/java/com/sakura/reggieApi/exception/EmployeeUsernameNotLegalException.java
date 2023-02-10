package com.sakura.reggieApi.exception;

/**
 * @author sakura
 * @className EmployeeUsenameNotLegalException
 * @createTime 2023/2/10
 */
public class EmployeeUsernameNotLegalException extends RuntimeException{
    public EmployeeUsernameNotLegalException(String message) {
        super(message);
    }
}
