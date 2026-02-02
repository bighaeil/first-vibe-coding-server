# 프로젝트 초기화 문서 (INIT.md)

## 1. 기술 스택

| 구분 | 기술 | 버전 |
|------|------|------|
| Language | Java | 17+ |
| Framework | Spring Boot | 3.2.x |
| Build Tool | Gradle (Kotlin DSL) | 8.x |
| Database | H2 (default) / MySQL (develop) | - |
| ORM | Spring Data JPA | 3.2.x |
| API 문서화 | SpringDoc OpenAPI | 2.3.x |
| 테스트 | JUnit 5 + Mockito | 5.10.x |

### 환경별 데이터베이스 설정

| Profile | Database | URL | 용도 |
|---------|----------|-----|------|
| default | H2 | jdbc:h2:mem:testdb | 로컬 개발/테스트 |
| develop | MySQL | jdbc:mysql://localhost:3306/testdb | 개발 서버 |

## 2. 프로젝트 구조

```
src/
├── main/
│   ├── java/com/example/project/
│   │   ├── ProjectApplication.java          # 메인 애플리케이션
│   │   ├── domain/                           # 도메인별 패키지
│   │   │   └── user/
│   │   │       ├── controller/
│   │   │       │   └── UserController.java
│   │   │       ├── service/
│   │   │       │   ├── UserService.java
│   │   │       │   └── UserServiceImpl.java
│   │   │       ├── repository/
│   │   │       │   └── UserRepository.java
│   │   │       ├── entity/
│   │   │       │   └── User.java
│   │   │       ├── dto/
│   │   │       │   ├── request/
│   │   │       │   │   └── UserCreateRequest.java
│   │   │       │   └── response/
│   │   │       │       └── UserResponse.java
│   │   │       └── exception/
│   │   │           └── UserNotFoundException.java
│   │   ├── global/                           # 공통 모듈
│   │   │   ├── config/                       # 설정 클래스
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── JpaConfig.java
│   │   │   │   └── SwaggerConfig.java
│   │   │   ├── exception/                    # 전역 예외 처리
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ErrorCode.java
│   │   │   │   └── ErrorResponse.java
│   │   │   ├── common/                       # 공통 클래스
│   │   │   │   ├── BaseEntity.java
│   │   │   │   └── ApiResponse.java
│   │   │   └── util/                         # 유틸리티
│   │   └── infra/                            # 외부 연동
│   │       ├── mail/
│   │       └── storage/
│   └── resources/
│       ├── application.yml            # default (H2)
│       └── application-develop.yml    # develop (MySQL)
└── test/
    └── java/com/example/project/
        ├── domain/
        │   └── user/
        │       ├── controller/
        │       │   └── UserControllerTest.java
        │       └── service/
        │           └── UserServiceTest.java
        └── integration/
            └── UserIntegrationTest.java
```

## 3. 코딩 컨벤션

### 네이밍 규칙

| 구분 | 규칙 | 예시 |
|------|------|------|
| 클래스 | PascalCase | `UserService`, `OrderController` |
| 메서드/변수 | camelCase | `findById()`, `userName` |
| 상수 | SCREAMING_SNAKE_CASE | `MAX_RETRY_COUNT` |
| 패키지 | lowercase | `com.example.domain.user` |
| DB 테이블/컬럼 | snake_case | `user_id`, `created_at` |

### 클래스 네이밍 패턴

```java
// Controller: ~Controller
UserController, OrderController

// Service 인터페이스: ~Service
UserService, OrderService

// Service 구현체: ~ServiceImpl
UserServiceImpl, OrderServiceImpl

// Repository: ~Repository
UserRepository, OrderRepository

// Entity: 도메인 명사
User, Order, Product

// DTO Request: ~Request
UserCreateRequest, UserUpdateRequest

// DTO Response: ~Response
UserResponse, UserListResponse

// Exception: ~Exception
UserNotFoundException, InvalidOrderException
```

### 메서드 네이밍 패턴

```java
// 조회
findById(), findAll(), findByEmail()
getUser()  // 단건 조회 (없으면 예외)
findUser() // 단건 조회 (Optional 반환)

// 생성
create(), save(), register()

// 수정
update(), modify()

// 삭제
delete(), remove()

// 검증
validate(), verify(), check()
isValid(), hasPermission()
```

## 4. 아키텍처 패턴

### Layered Architecture (Controller → Service → Repository)

```
┌─────────────────────────────────────────────────────────┐
│                    Controller Layer                      │
│  - HTTP 요청/응답 처리                                    │
│  - 입력 검증 (@Valid)                                    │
│  - DTO ↔ 도메인 변환                                     │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                     Service Layer                        │
│  - 비즈니스 로직 처리                                     │
│  - 트랜잭션 관리 (@Transactional)                        │
│  - 도메인 객체 조작                                       │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                   Repository Layer                       │
│  - 데이터 접근 로직                                       │
│  - JPA/QueryDSL 쿼리                                     │
│  - 영속성 관리                                            │
└─────────────────────────────────────────────────────────┘
```

### 의존성 규칙

- Controller → Service (인터페이스)
- Service → Repository
- 상위 계층은 하위 계층만 의존
- Entity는 어떤 계층도 의존하지 않음

## 5. 응답 형식

### 공통 응답 Wrapper

```java
@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
```

### 성공 응답 예시

```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "createdAt": "2024-01-15T10:30:00"
  },
  "message": null
}
```

### 에러 응답 형식

```java
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;
    private final List<FieldError> errors;  // 유효성 검증 에러

    @Getter
    @AllArgsConstructor
    public static class FieldError {
        private final String field;
        private final String reason;
    }
}
```

### 에러 응답 예시

```json
{
  "code": "USER_NOT_FOUND",
  "message": "사용자를 찾을 수 없습니다.",
  "timestamp": "2024-01-15T10:30:00",
  "errors": null
}
```

## 6. API 설계 원칙

### RESTful 엔드포인트 규칙

| 동작 | HTTP Method | URL 패턴 | 예시 |
|------|-------------|----------|------|
| 목록 조회 | GET | /api/v1/{resources} | GET /api/v1/users |
| 단건 조회 | GET | /api/v1/{resources}/{id} | GET /api/v1/users/1 |
| 생성 | POST | /api/v1/{resources} | POST /api/v1/users |
| 수정 | PUT/PATCH | /api/v1/{resources}/{id} | PUT /api/v1/users/1 |
| 삭제 | DELETE | /api/v1/{resources}/{id} | DELETE /api/v1/users/1 |

### URL 작성 규칙

- 소문자 사용
- 복수형 명사 사용 (`/users`, `/orders`)
- 하이픈(-) 사용 (언더스코어 지양)
- 계층 관계: `/users/{userId}/orders`
- 쿼리 파라미터: 필터링, 정렬, 페이징에 사용

### Controller 작성 예시

```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<Page<UserResponse>> getUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
        return ApiResponse.success(userService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.success(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }
}
```

## 7. 에러 처리

### ErrorCode Enum 정의

```java
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(400, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(500, "C002", "서버 내부 오류가 발생했습니다."),

    // User
    USER_NOT_FOUND(404, "U001", "사용자를 찾을 수 없습니다."),
    EMAIL_DUPLICATED(409, "U002", "이미 존재하는 이메일입니다."),

    // Auth
    UNAUTHORIZED(401, "A001", "인증이 필요합니다."),
    ACCESS_DENIED(403, "A002", "접근 권한이 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
```

### 커스텀 예외 클래스

```java
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

// 사용 예시
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
```

### 전역 예외 처리

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ErrorResponse.of(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e) {
        log.warn("Validation exception: {}", e.getMessage());
        return ResponseEntity
            .badRequest()
            .body(ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE,
                                   e.getBindingResult()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected exception", e);
        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
```

## 8. 테스트 전략

### 테스트 레이어별 전략

| 레이어 | 테스트 유형 | 도구 | 범위 |
|--------|-------------|------|------|
| Controller | 슬라이스 테스트 | @WebMvcTest + MockMvc | HTTP 요청/응답 |
| Service | 단위 테스트 | @ExtendWith(MockitoExtension.class) | 비즈니스 로직 |
| Repository | 슬라이스 테스트 | @DataJpaTest | 쿼리 검증 |
| 통합 | 통합 테스트 | @SpringBootTest | 전체 플로우 |

### Service 단위 테스트 예시

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("ID로 사용자 조회 성공")
    void findById_Success() {
        // given
        Long userId = 1L;
        User user = User.builder()
            .id(userId)
            .email("test@example.com")
            .name("테스트")
            .build();
        given(userRepository.findById(userId))
            .willReturn(Optional.of(user));

        // when
        UserResponse result = userService.findById(userId);

        // then
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        then(userRepository).should().findById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 예외 발생")
    void findById_NotFound_ThrowsException() {
        // given
        Long userId = 999L;
        given(userRepository.findById(userId))
            .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findById(userId))
            .isInstanceOf(UserNotFoundException.class);
    }
}
```

### Controller 슬라이스 테스트 예시

```java
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("사용자 생성 API")
    void createUser_Success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(
            "test@example.com", "테스트", "password123"
        );
        UserResponse response = new UserResponse(1L, "test@example.com", "테스트");
        given(userService.create(any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }
}
```

## 9. 보안 가이드라인

### 필수 보안 체크리스트

- [ ] **입력 검증**: 모든 사용자 입력에 @Valid 적용
- [ ] **SQL Injection 방지**: JPA 파라미터 바인딩 사용 (직접 문자열 연결 금지)
- [ ] **XSS 방지**: 응답 데이터 이스케이프 처리
- [ ] **인증/인가**: Spring Security 기반 구현
- [ ] **비밀번호**: BCrypt 암호화 저장
- [ ] **민감정보**: 환경변수 또는 Vault로 ��리 (코드에 하드코딩 금지)
- [ ] **CORS**: 허용된 도메인만 명시적 설정
- [ ] **Rate Limiting**: API 호출 제한 적용
- [ ] **로깅**: 민감정보(비밀번호, 토큰) 로그 출력 금지

### 입력 검증 예시

```java
public record UserCreateRequest(
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 20, message = "이름은 2~20자 사이여야 합니다.")
    String name,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
             message = "비밀번호는 8자 이상, 영문과 숫자를 포함해야 합니다.")
    String password
) {}
```

## 10. 기타 규칙

### Entity 작성 규칙

```java
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private String password;

    @Builder
    private User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    // 비즈니스 메서드
    public void updateName(String name) {
        this.name = name;
    }
}
```

### BaseEntity (공통 필드)

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

---

## AI 협업 시 유의사항

### AI에게 요청할 때

1. **명확한 제약조건 제시**
   - "User 엔티티 생성해줘" (X)
   - "User 엔티티 생성해줘. email은 unique, name은 50자 제한, BaseEntity 상속" (O)

2. **기존 코드 스타일 유지 명시**
   - "기존 프로젝트의 패키지 구조와 네이밍 컨벤션을 따라서 작성해줘"

3. **단계별 구현 요청**
   - Entity → Repository → Service → Controller 순서로 요청

### AI 생성 코드 검토 포인트

- [ ] N+1 쿼리 문제 없는지 확인
- [ ] 트랜잭션 범위 적절한지 확인
- [ ] 예외 처리 누락 없는지 확인
- [ ] 보안 취약점 없는지 확인
- [ ] 테스트 코드 커버리지 충분한지 확인
