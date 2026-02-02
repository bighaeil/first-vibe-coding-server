package com.example.board.domain.post.exception;

import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;

public class PasswordMismatchException extends BusinessException {

    public PasswordMismatchException() {
        super(ErrorCode.PASSWORD_MISMATCH);
    }
}
