package com.example.board.domain.post.exception;

import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;

public class PostNotFoundException extends BusinessException {

    public PostNotFoundException() {
        super(ErrorCode.POST_NOT_FOUND);
    }
}
