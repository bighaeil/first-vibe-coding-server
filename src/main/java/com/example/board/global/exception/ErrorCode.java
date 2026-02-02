package com.example.board.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(500, "C002", "서버 내부 오류가 발생했습니다."),

    // Post
    POST_NOT_FOUND(404, "P001", "게시글을 찾을 수 없습니다."),
    PASSWORD_MISMATCH(401, "P002", "비밀번호가 일치하지 않습니다.");

    private final int status;
    private final String code;
    private final String message;
}
