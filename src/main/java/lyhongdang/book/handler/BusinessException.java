package lyhongdang.book.handler;

import lombok.Getter;
import lombok.Setter;
import lyhongdang.book.enums.ErrorCodes;

@Getter
@Setter
public class BusinessException extends RuntimeException {
    private ErrorCodes errorCode;
    public BusinessException(ErrorCodes errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}