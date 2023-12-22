package com.prgrms.catchtable.common.exception.custom;

import com.prgrms.catchtable.common.exception.CommonException;
import com.prgrms.catchtable.common.exception.ErrorCode;

public class BadRequestCustomException extends CommonException {

    public BadRequestCustomException(ErrorCode errorCode) {
        super(errorCode);
    }
}
