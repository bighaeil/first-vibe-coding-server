package com.example.board.domain.post.dto.response;

import com.example.board.domain.post.entity.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String author,
        String title,
        String content,
        Integer viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getAuthor(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
