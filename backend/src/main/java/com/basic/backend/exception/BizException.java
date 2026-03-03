package com.basic.backend.exception;

import com.basic.backend.common.ResultCode;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        super(message);
        this.code = ResultCode.BIZ_ERROR.code();
    }

    public BizException(ResultCode resultCode) {
        super(resultCode.message());
        this.code = resultCode.code();
    }

    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.code();
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
}