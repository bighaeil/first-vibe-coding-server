package com.example.board.domain.post.controller;

import com.example.board.domain.post.dto.request.PostCreateRequest;
import com.example.board.domain.post.dto.request.PostDeleteRequest;
import com.example.board.domain.post.dto.request.PostUpdateRequest;
import com.example.board.domain.post.dto.response.PostListResponse;
import com.example.board.domain.post.dto.response.PostResponse;
import com.example.board.domain.post.service.PostService;
import com.example.board.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "Post", description = "게시글 API")
public class PostController {

    private final PostService postService;

    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 페이징하여 조회합니다.")
    public ApiResponse<Page<PostListResponse>> getPosts(
            @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.success(postService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 내용을 조회합니다.")
    public ApiResponse<PostResponse> getPost(@PathVariable Long id) {
        return ApiResponse.success(postService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "게시글 작성", description = "새로운 게시글을 등록합니다.")
    public ApiResponse<PostResponse> createPost(
            @Valid @RequestBody PostCreateRequest request) {
        return ApiResponse.success(postService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "기존 게시글의 내용을 수정합니다.")
    public ApiResponse<PostResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateRequest request) {
        return ApiResponse.success(postService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    public void deletePost(
            @PathVariable Long id,
            @Valid @RequestBody PostDeleteRequest request) {
        postService.delete(id, request);
    }
}
