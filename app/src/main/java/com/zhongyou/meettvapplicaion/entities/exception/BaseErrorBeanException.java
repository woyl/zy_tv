package com.zhongyou.meettvapplicaion.entities.exception;

/**
 * Created by wufan on 2017/2/14.
 */

public class BaseErrorBeanException extends Exception {
    public BaseErrorBeanException() {
    }

    public BaseErrorBeanException(String message) {
        super(message);
    }
}
