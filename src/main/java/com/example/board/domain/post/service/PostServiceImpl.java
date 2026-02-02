package com.example.board.domain.post.service;

import com.example.board.domain.post.dto.request.PostCreateRequest;
import com.example.board.domain.post.dto.request.PostDeleteRequest;
import com.example.board.domain.post.dto.request.PostUpdateRequest;
import com.example.board.domain.post.dto.response.PostListResponse;
import com.example.board.domain.post.dto.response.PostResponse;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.exception.PasswordMismatchException;
import com.example.board.domain.post.exception.PostNotFoundException;
import com.example.board.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<PostListResponse> findAll(Pageable pageable) {
        return postRepository.findByDeletedFalseOrderByCreatedAtDesc(pageable)
                .map(PostListResponse::from);
    }

    @Override
    @Transactional
    public PostResponse findById(Long id) {
        Post post = getPost(id);
        post.increaseViewCount();
        return PostResponse.from(post);
    }

    @Override
    @Transactional
    public PostResponse create(PostCreateRequest request) {
        Post post = Post.builder()
                .author(request.author())
                .password(passwordEncoder.encode(request.password()))
                .title(request.title())
                .content(request.content())
                .build();

        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }

    @Override
    @Transactional
    public PostResponse update(Long id, PostUpdateRequest request) {
        Post post = getPost(id);
        validatePassword(request.password(), post.getPassword());

        post.update(request.title(), request.content());
        return PostResponse.from(post);
    }

    @Override
    @Transactional
    public void delete(Long id, PostDeleteRequest request) {
        Post post = getPost(id);
        validatePassword(request.password(), post.getPassword());

        post.delete();
    }

    private Post getPost(Long id) {
        return postRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(PostNotFoundException::new);
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new PasswordMismatchException();
        }
    }
}
