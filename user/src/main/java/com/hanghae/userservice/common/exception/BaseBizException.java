package com.hanghae.userservice.common.exception;

import lombok.Getter;

@Getter
public class BaseBizException extends RuntimeException {

    private String errorCode;
    private String errorMessage;

    public BaseBizException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public BaseBizException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    };

    public BaseBizException(String errorCode, String errorMessage, Throwable cause) {
        super(cause.getMessage(), cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    };

    public BaseBizException(String errorMessage, Throwable cause) {
        super(cause.getMessage(), cause);
        this.errorMessage = errorMessage;
    }

    public BaseBizException(Throwable cause) {
        super(cause);
    }

}
