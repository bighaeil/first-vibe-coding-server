package com.example.board.domain.post.dto.response;

import com.example.board.domain.post.entity.Post;

import java.time.LocalDateTime;

public record PostListResponse(
        Long id,
        String author,
        String title,
        Integer viewCount,
        LocalDateTime createdAt
) {
    public static PostListResponse from(Post post) {
        return new PostListResponse(
                post.getId(),
                post.getAuthor(),
                post.getTitle(),
                post.getViewCount(),
                post.getCreatedAt()
        );
    }
}
