# 제품 요구사항 문서 (PRD.md)

## 1. 프로젝트 개요

### 1.1 배경
간단한 익명 게시판 서비스의 백엔드 API를 개발합니다. 회원가입/로그인 없이 누구나 자유롭게 게시글을 작성하고 조회할 수 있는 서비스입니다.

### 1.2 목표
- 게시글 CRUD 기능을 제공하는 RESTful API 서버 구축
- 심플하고 확장 가능한 구조 설계
- API 문서 자동화 (Swagger/OpenAPI)

### 1.3 성공 지표
- 모든 CRUD API 정상 동작
- API 응답 시간 200ms 이하
- 테스트 커버리지 80% 이상

---

## 2. 사용자 스토리

| ID | 역할 | 기능 | 목적 |
|----|------|------|------|
| US-01 | 사용자 | 게시글 목록을 조회한다 | 어떤 글들이 있는지 확인하기 위해 |
| US-02 | 사용자 | 게시글 상세 내용을 조회한다 | 특정 글의 전체 내용을 읽기 위해 |
| US-03 | 사용자 | 새 게시글을 작성한다 | 나의 글을 공유하기 위해 |
| US-04 | 사용자 | 게시글을 수정한다 | 작성한 글의 내용을 변경하기 위해 |
| US-05 | 사용자 | 게시글을 삭제한다 | 더 이상 필요 없는 글을 제거하기 위해 |

---

## 3. 기능 명세

### 3.1 게시글 목록 조회

**설명**: 등록된 게시글 목록을 페이징하여 조회합니다.

**비즈니스 로직**:
- 최신순(작성일 내림차순)으로 정렬
- 페이지당 기본 10건 조회
- 삭제된 게시글은 목록에서 제외

**제약조건**:
- 페이지 번호: 0 이상
- 페이지 크기: 1 ~ 100

### 3.2 게시글 상세 조회

**설명**: 특정 게시글의 상세 내용을 조회합니다.

**비즈니스 로직**:
- 게시글 ID로 조회
- 조회 시 조회수 1 증가

**예외 처리**:
- 존재하지 않는 게시글 ID → 404 Not Found

### 3.3 게시글 작성

**설명**: 새로운 게시글을 등록합니다.

**비즈니스 로직**:
- 작성자명, 비밀번호, 제목, 내용 입력 필수
- 비밀번호는 암호화하여 저장
- 작성일시 자동 기록

**제약조건**:
| 필드 | 제약조건 |
|------|----------|
| 작성자명 | 필수, 2~20자 |
| 비밀번호 | 필수, 4~20자 |
| 제목 | 필수, 1~100자 |
| 내용 | 필수, 1~5000자 |

### 3.4 게시글 수정

**설명**: 기존 게시글의 내용을 수정합니다.

**비즈니스 로직**:
- 비밀번호 검증 후 수정 허용
- 제목, 내용만 수정 가능
- 수정일시 자동 기록

**예외 처리**:
- 존재하지 않는 게시글 → 404 Not Found
- 비밀번호 불일치 → 401 Unauthorized

### 3.5 게시글 삭제

**설명**: 게시글을 삭제합니다.

**비즈니스 로직**:
- 비밀번호 검증 후 삭제 허용
- Soft Delete (삭제 플래그 처리)

**예외 처리**:
- 존재하지 않는 게시글 → 404 Not Found
- 비밀번호 불일치 → 401 Unauthorized

---

## 4. 데이터 모델

### 4.1 ERD

```
┌─────────────────────────────────────┐
│              POST                   │
├─────────────────────────────────────┤
│ id           BIGINT      PK, AI     │
│ author       VARCHAR(20) NOT NULL   │
│ password     VARCHAR(100) NOT NULL  │
│ title        VARCHAR(100) NOT NULL  │
│ content      TEXT        NOT NULL   │
│ view_count   INT         DEFAULT 0  │
│ deleted      BOOLEAN     DEFAULT F  │
│ created_at   TIMESTAMP   NOT NULL   │
│ updated_at   TIMESTAMP              │
└─────────────────────────────────────┘
```

### 4.2 Entity 설계

```java
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String author;        // 작성자명

    @Column(nullable = false, length = 100)
    private String password;      // 암호화된 비밀번호

    @Column(nullable = false, length = 100)
    private String title;         // 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;       // 내용

    @Column(nullable = false)
    private Integer viewCount = 0; // 조회수

    @Column(nullable = false)
    private Boolean deleted = false; // 삭제 여부
}
```

---

## 5. API 명세

### 5.1 Base URL

```
/api/v1/posts
```

### 5.2 엔드포인트 목록

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | /api/v1/posts | 게시글 목록 조회 |
| GET | /api/v1/posts/{id} | 게시글 상세 조회 |
| POST | /api/v1/posts | 게시글 작성 |
| PUT | /api/v1/posts/{id} | 게시글 수정 |
| DELETE | /api/v1/posts/{id} | 게시글 삭제 |

### 5.3 상세 API 명세

#### 5.3.1 게시글 목록 조회

```
GET /api/v1/posts?page=0&size=10
```

**Query Parameters**:
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| page | int | N | 0 | 페이지 번호 (0부터 시작) |
| size | int | N | 10 | 페이지 크기 |

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "author": "홍길동",
        "title": "첫 번째 게시글입니다",
        "viewCount": 10,
        "createdAt": "2024-01-15T10:30:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  },
  "message": null
}
```

#### 5.3.2 게시글 상세 조회

```
GET /api/v1/posts/{id}
```

**Path Parameters**:
| 파라미터 | 타입 | 설명 |
|----------|------|------|
| id | long | 게시글 ID |

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "author": "홍길동",
    "title": "첫 번째 게시글입니다",
    "content": "게시글 내용입니다...",
    "viewCount": 11,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": null
  },
  "message": null
}
```

**Response (404 Not Found)**:
```json
{
  "code": "POST_NOT_FOUND",
  "message": "게시글을 찾을 수 없습니다.",
  "timestamp": "2024-01-15T10:30:00",
  "errors": null
}
```

#### 5.3.3 게시글 작성

```
POST /api/v1/posts
```

**Request Body**:
```json
{
  "author": "홍길동",
  "password": "1234",
  "title": "첫 번째 게시글입니다",
  "content": "게시글 내용입니다..."
}
```

**Validation**:
| 필드 | 규칙 |
|------|------|
| author | @NotBlank, @Size(min=2, max=20) |
| password | @NotBlank, @Size(min=4, max=20) |
| title | @NotBlank, @Size(max=100) |
| content | @NotBlank, @Size(max=5000) |

**Response (201 Created)**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "author": "홍길동",
    "title": "첫 번째 게시글입니다",
    "content": "게시글 내용입니다...",
    "viewCount": 0,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": null
  },
  "message": null
}
```

#### 5.3.4 게시글 수정

```
PUT /api/v1/posts/{id}
```

**Request Body**:
```json
{
  "password": "1234",
  "title": "수정된 제목입니다",
  "content": "수정된 내용입니다..."
}
```

**Validation**:
| 필드 | 규칙 |
|------|------|
| password | @NotBlank |
| title | @NotBlank, @Size(max=100) |
| content | @NotBlank, @Size(max=5000) |

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "author": "홍길동",
    "title": "수정된 제목입니다",
    "content": "수정된 내용입니다...",
    "viewCount": 11,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T11:00:00"
  },
  "message": null
}
```

**Response (401 Unauthorized)**:
```json
{
  "code": "PASSWORD_MISMATCH",
  "message": "비밀번호가 일치하지 않습니다.",
  "timestamp": "2024-01-15T10:30:00",
  "errors": null
}
```

#### 5.3.5 게시글 삭제

```
DELETE /api/v1/posts/{id}
```

**Request Body**:
```json
{
  "password": "1234"
}
```

**Response (204 No Content)**:
(응답 본문 없음)

**Response (401 Unauthorized)**:
```json
{
  "code": "PASSWORD_MISMATCH",
  "message": "비밀번호가 일치하지 않습니다.",
  "timestamp": "2024-01-15T10:30:00",
  "errors": null
}
```

---

## 6. 비기능 요구사항

### 6.1 성능
- API 응답 시간: 평균 200ms 이하
- 동시 접속: 100명 이상 처리 가능

### 6.2 보안
- 비밀번호 BCrypt 암호화 저장
- SQL Injection 방지 (JPA 파라미터 바인딩)
- XSS 방지

### 6.3 문서화
- Swagger UI 제공 (`/swagger-ui.html`)
- OpenAPI 3.0 스펙 지원

### 6.4 로깅
- 요청/응답 로그 기록
- 에러 발생 시 스택 트레이스 기록

---

## 7. 기술 스택 (요약)

| 구분 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Database | H2 (default) / MySQL (develop) |
| ORM | Spring Data JPA |
| API Docs | SpringDoc OpenAPI |
| Build | Gradle |

### 7.1 환경별 설정

| 환경 | Profile | Database | 설명 |
|------|---------|----------|------|
| 기본 | default | H2 (In-Memory) | 로컬 개발, 테스트용 |
| 개발 | develop | MySQL | 개발 서버 환경 |

#### default (기본 환경)
```yaml
# application.yml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

#### develop (개발 환경)
```yaml
# application-develop.yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/testdb
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

#### 실행 방법
```bash
# default (H2)
./gradlew bootRun

# develop (MySQL)
./gradlew bootRun --args='--spring.profiles.active=develop'
```

---

## 8. 마일스톤

| 단계 | 내용 |
|------|------|
| Phase 1 | 프로젝트 초기 설정, Entity/Repository 구현 |
| Phase 2 | Service/Controller 구현, CRUD API 완성 |
| Phase 3 | 예외 처리, 유효성 검증, API 문서화 |
| Phase 4 | 테스트 코드 작성, 리팩토링 |
