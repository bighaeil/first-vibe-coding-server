package com.example.board.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private final String code;
    private final String message;
    private final LocalDateTime timestamp;
    private final List<FieldError> errors;

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                LocalDateTime.now(),
                null
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                LocalDateTime.now(),
                FieldError.of(bindingResult)
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, String detailMessage) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage() + " - " + detailMessage,
                LocalDateTime.now(),
                null
        );
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FieldError {
        private final String field;
        private final String reason;

        private static List<FieldError> of(BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getDefaultMessage()
                    ))
                    .toList();
        }
    }
}
