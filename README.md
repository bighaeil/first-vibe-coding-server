# Board API

간단한 익명 게시판 RESTful API 서버입니다.

## 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.2 |
| Database | H2 (default) / MySQL (develop) |
| ORM | Spring Data JPA |
| Build | Gradle (Kotlin DSL) |
| API Docs | SpringDoc OpenAPI (Swagger) |

## 프로젝트 구조

```
src/main/java/com/example/board/
├── BoardApplication.java
├── domain/post/
│   ├── controller/PostController.java
│   ├── service/PostService.java, PostServiceImpl.java
│   ├── repository/PostRepository.java
│   ├── entity/Post.java
│   ├── dto/request/
│   └── dto/response/
└── global/
    ├── common/ApiResponse.java, BaseEntity.java
    ├── config/
    └── exception/
```

## 실행 방법

```bash
# default (H2 In-Memory)
./gradlew bootRun

# develop (MySQL)
./gradlew bootRun --args='--spring.profiles.active=develop'
```

## API 엔드포인트

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | /api/v1/posts | 게시글 목록 조회 |
| GET | /api/v1/posts/{id} | 게시글 상세 조회 |
| POST | /api/v1/posts | 게시글 작성 |
| PUT | /api/v1/posts/{id} | 게시글 수정 |
| DELETE | /api/v1/posts/{id} | 게시글 삭제 |

## API 사용 예시

### 게시글 작성
```bash
curl -X POST http://localhost:8080/api/v1/posts \
  -H "Content-Type: application/json" \
  -d '{"author":"홍길동","password":"1234","title":"제목","content":"내용"}'
```

### 게시글 수정
```bash
curl -X PUT http://localhost:8080/api/v1/posts/1 \
  -H "Content-Type: application/json" \
  -d '{"password":"1234","title":"수정된 제목","content":"수정된 내용"}'
```

### 게시글 삭제
```bash
curl -X DELETE http://localhost:8080/api/v1/posts/1 \
  -H "Content-Type: application/json" \
  -d '{"password":"1234"}'
```

## 접속 URL

- API: `http://localhost:8080/api/v1/posts`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console` (default 환경)

## 환경별 설정

| Profile | Database | URL |
|---------|----------|-----|
| default | H2 | jdbc:h2:mem:testdb |
| develop | MySQL | jdbc:mysql://localhost:3306/testdb |
