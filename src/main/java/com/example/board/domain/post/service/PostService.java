package com.example.board.domain.post.service;

import com.example.board.domain.post.dto.request.PostCreateRequest;
import com.example.board.domain.post.dto.request.PostDeleteRequest;
import com.example.board.domain.post.dto.request.PostUpdateRequest;
import com.example.board.domain.post.dto.response.PostListResponse;
import com.example.board.domain.post.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    Page<PostListResponse> findAll(Pageable pageable);

    PostResponse findById(Long id);

    PostResponse create(PostCreateRequest request);

    PostResponse update(Long id, PostUpdateRequest request);

    void delete(Long id, PostDeleteRequest request);
}
