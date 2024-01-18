package com.prgrms.catchtable.common.exception.custom;

import com.prgrms.catchtable.common.exception.CommonException;
import com.prgrms.catchtable.common.exception.ErrorCode;

public class NotFoundCustomException extends CommonException {

    public NotFoundCustomException(ErrorCode errorCode) {
        super(errorCode);
    }
}
