package com.example.board.domain.post.entity;

import com.example.board.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String author;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Builder
    private Post(String author, String password, String title, String content) {
        this.author = author;
        this.password = password;
        this.title = title;
        this.content = content;
        this.viewCount = 0;
        this.deleted = false;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void delete() {
        this.deleted = true;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}
