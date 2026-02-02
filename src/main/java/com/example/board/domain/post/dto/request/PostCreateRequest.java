package com.example.board.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateRequest(
        @NotBlank(message = "작성자명은 필수입니다.")
        @Size(min = 2, max = 20, message = "작성자명은 2~20자 사이여야 합니다.")
        String author,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 4, max = 20, message = "비밀번호는 4~20자 사이여야 합니다.")
        String password,

        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
        String title,

        @NotBlank(message = "내용은 필수입니다.")
        @Size(max = 5000, message = "내용은 5000자 이하여야 합니다.")
        String content
) {
}
