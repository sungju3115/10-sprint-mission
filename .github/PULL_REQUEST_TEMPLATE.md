# 프로파일 기반 설정 관리

- [X] 개발, 운영 환경에 대한 프로파일을 구성하세요.
    - [X] application-dev.yaml, application-prod.yaml 파일을 생성하세요.
    - [X] 다음과 같은 설정값을 프로파일별로 분리하세요.
        - [X] 데이터베이스 연결 정보
        - [X] 서버 포트

---

# 로그 관리

- [X] Lombok의 @Slf4j 어노테이션을 활용해 로깅을 쉽게 추가할 수 있도록 구성하세요.
- [X] application.yaml에 기본 로깅 레벨을 설정하세요.
    - 기본적으로 info 레벨로 설정합니다.
- [X] 환경 별 적절한 로깅 레벨을 프로파일 별로 설정해보세요.
    - SQL 로그를 보기위해 설정했던 레벨은 유지합니다.
    - 우리가 작성한 프로젝트의 로그는 개발 환경에서 debug, 운영 환경에서는 info 레벨로 설정합니다.
- [X] Spring Boot의 기본 로깅 구현체인 Logback의 설정 파일을 구성하세요.
    - [X] logback-spring.xml 파일을 생성하세요.
- [X] 다음 예시와 같은 로그 메시지를 출력하기 위한 로깅 패턴과 출력 방식을 커스터마이징하세요.

  **로그 출력 예시**

    ```
    # 패턴
    {년}-{월}-{일} {시}:{분}:{초}:{밀리초} [{스레드명}] {로그 레벨(5글자로 맞춤)} {로거 이름(최대 36글자)} - {로그 메시지}{줄바꿈}

    # 예시
    25-01-01 10:33:55.740 [main] DEBUG c.s.m.discodeit.DiscodeitApplication - Running with Spring Boot v3.4.0, Spring v6.2.0
    ```

- [X] 콘솔과 파일에 동시에 로그를 기록하도록 설정하세요.
    - [X] 파일은 `{프로젝트 루트}/.logs` 경로에 저장되도록 설정하세요.
    - [X] 로그 파일은 일자별로 롤링되도록 구성하세요.
    - [X] 로그 파일은 30일간 보관하도록 구성하세요.
- [X] 서비스 레이어와 컨트롤러 레이어의 주요 메소드에 로깅을 추가하세요.
    - [X] 로깅 레벨을 적절히 사용하세요: ERROR, WARN, INFO, DEBUG
    - [X] 다음과 같은 메소드에 로깅을 추가하세요:
        - [X] 사용자 생성/수정/삭제
        - [X] 채널 생성/수정/삭제
        - [X] 메시지 생성/수정/삭제
        - [X] 파일 업로드/다운로드

---

# 예외 처리 고도화

- [X] 커스텀 예외를 설계하고 구현하세요.
    - 패키지명: `com.sprint.mission.discodeit.exception[.{도메인}]`
- [X] ErrorCode Enum 클래스를 통해 예외 코드명과 메시지를 정의하세요.
    - 아래는 예시입니다. 필요하다고 판단되는 다양한 코드를 정의하세요.
- [X] 모든 예외의 기본이 되는 DiscodeitException 클래스를 정의하세요.
    - `details`는 예외 발생 상황에 대한 추가정보를 저장하기 위한 속성입니다.
    - 예시:
        - 조회 시도한 사용자의 ID 정보
        - 업데이트 시도한 PRIVATE 채널의 ID 정보
- [X] DiscodeitException을 상속하는 주요 도메인 별 메인 예외 클래스를 정의하세요.
    - UserException, ChannelException 등
    - 실제로 활용되는 클래스라기보다는 예외 클래스의 계층 구조를 명확하게 하기 위한 클래스 입니다.
- [X] 도메인 메인 예외 클래스를 상속하는 구체적인 예외 클래스를 정의하세요.
    - UserNotFoundException, UserAlreadyExistException 등 필요한 예외를 정의하세요.
- [X] 기존에 구현했던 예외를 커스텀 예외로 대체하세요.
    - NoSuchElementException
    - IllegalArgumentException
    - …
- [X] ErrorResponse를 통해 일관된 예외 응답을 정의하세요.
    - `int status`: HTTP 상태코드
    - `String exceptionType`: 발생한 예외의 클래스 이름
- [X] 앞서 정의한 ErrorResponse와 @RestControllerAdvice를 활용해 예외를 처리하는 예외 핸들러를 구현하세요.
    - 모든 핸들러는 일관된 응답(ErrorResponse)을 가져야 합니다.

---

# 유효성 검사

- [X] Spring Validation 의존성을 추가하세요.
- [X] 주요 Request DTO에 제약 조건 관련 어노테이션을 추가하세요.
    - @NotNull, @NotBlank, @Size, @Email 등
- [X] 컨트롤러에 @Valid 를 사용해 요청 데이터를 검증하세요.
- [X] 검증 실패 시 발생하는 MethodArgumentNotValidException을 전역 예외 핸들러에서 처리하세요.
- [X] 유효성 검증 실패 시 상세한 오류 메시지를 포함한 응답을 반환하세요.

---

# Actuator

- [X] Spring Boot Actuator 의존성을 추가하세요.
- [X] 기본 Actuator 엔드포인트를 설정하세요.
    - health, info, metrics, loggers
- [X] Actuator info를 위한 애플리케이션 정보를 추가하세요.
    - 애플리케이션 이름: Discodeit
    - 애플리케이션 버전: 1.7.0
    - 자바 버전: 17
    - 스프링 부트 버전: 3.4.0
    - 주요 설정 정보
        - 데이터소스: url, 드라이버 클래스 이름
        - jpa: ddl-auto
        - storage 설정: type, path
        - multipart 설정: max-file-size, max-request-size
- [X] Spring Boot 서버를 실행 후 각종 정보를 확인해보세요.
    - /actuator/info
    - /actuator/metrics
    - /actuator/health
    - /actuator/loggers

---

# 단위 테스트

- [X] 서비스 레이어의 주요 메소드에 대한 단위 테스트를 작성하세요.
- [X] 다음 서비스의 핵심 메소드에 대해 각각 최소 2개 이상(성공, 실패)의 테스트 케이스를 작성하세요.
    - [X] UserService: create, update, delete 메소드
    - [X] ChannelService: create(PUBLIC, PRIVATE), update, delete, findByUserId 메소드
    - [X] MessageService: create, update, delete, findByChannelId 메소드
- [X] Mockito를 활용해 Repository 의존성을 모의(mock)하세요.
- [X] BDDMockito를 활용해 테스트 가독성을 높이세요.

---

# 슬라이스 테스트

- [ ] 레포지토리 레이어의 슬라이스 테스트를 작성하세요.
    - [X] @DataJpaTest를 활용해 테스트를 구현하세요.
    - [X] 테스트 환경을 구성하는 프로파일을 구성하세요.
        - [X] application-test.yaml을 생성하세요.
        - [X] 데이터소스는 H2 인메모리 데이터 베이스를 사용하고, PostgreSQL 호환 모드로 설정하세요.
        - [X] H2 데이터베이스를 위해 필요한 의존성을 추가하세요.
        - [X] 테스트 시작 시 스키마를 새로 생성하도록 설정하세요.
        - [X] 디버깅에 용이하도록 로그 레벨을 적절히 설정하세요.
    - [X] 테스트 실행 간 test 프로파일을 활성화 하세요.
    - [X] JPA Audit 기능을 활성화 하기 위해 테스트 클래스에 @EnableJpaAuditing을 추가하세요.
    - [X] 주요 레포지토리(User, Channel, Message)의 주요 쿼리 메소드에 대해 각각 최소 2개 이상(성공, 실패)의 테스트 케이스를 작성하세요.
        - [X] 커스텀 쿼리 메소드
        - [X] 페이징 및 정렬 메소드
- [X] 컨트롤러 레이어의 슬라이스 테스트를 작성하세요.
    - [X] @WebMvcTest를 활용해 테스트를 구현하세요.
    - [X] WebMvcTest에서 자동으로 등록되지 않는 유형의 Bean이 필요하다면 @Import를 활용해 추가하세요.

      예시:
        ```java
        @Import({ErrorCodeStatusMapper.class})
        ```

    - [X] 주요 컨트롤러(User, Channel, Message)에 대해 최소 2개 이상(성공, 실패)의 테스트 케이스를 작성하세요.
    - [X] MockMvc를 활용해 컨트롤러를 테스트하세요.
    - [X] 서비스 레이어를 모의(mock)하여 컨트롤러 로직만 테스트하세요.
    - [X] JSON 응답을 검증하는 테스트를 포함하세요.

---

# 통합 테스트

- [ ] 통합 테스트 환경을 구성하세요.
    - [ ] @SpringBootTest를 활용해 Spring 애플리케이션 컨텍스트를 로드하세요.
    - [ ] H2 인메모리 데이터베이스를 활용하세요.
    - [ ] 테스트용 프로파일을 구성하세요.
- [ ] 주요 API 엔드포인트에 대한 통합 테스트를 작성하세요.
    - [ ] 주요 API에 대해 최소 2개 이상의 테스트 케이스를 작성하세요.
    - [ ] 사용자 관련 API (생성, 수정, 삭제, 목록 조회)
    - [ ] 채널 관련 API (생성, 수정, 삭제)
    - [ ] 메시지 관련 API (생성, 수정, 삭제, 목록 조회)
    - [ ] 각 테스트는 @Transactional을 활용해 독립적으로 실행하세요.

### 궁금한 점
- IoException 같은 것은 굳이 Custom 예외로 정의해서 반환해야할까??
- Actuator에 Git 정보 연동 하려고 했는데 호환이 안되서 맞는 버전이 뭘까?? 
- JPA 더티체킹, 명시적 save는 무슨 차이가 있을까 ?? 
                                                                                                                                                                                                               
  ---                                                                                                                                                                                                          
    1. Spring 테스트 슬라이싱 (Test Slicing)

  Spring은 전체 컨텍스트를 로드하는 대신 레이어별로 쪼개서 테스트할 수 있습니다.

  ┌─────────────────┬───────────────────┬────────────────────┐                                                                                                                                                 
  │   어노테이션    │     로드 대상     │ 주로 테스트하는 것 │                                                                                                                                               
  ├─────────────────┼───────────────────┼────────────────────┤                                                                                                                                                 
  │ @SpringBootTest │ 전체              │ 통합 테스트        │                                                                                                                                                 
  ├─────────────────┼───────────────────┼────────────────────┤                                                                                                                                                 
  │ @WebMvcTest     │ 웹 레이어만       │ Controller         │                                                                                                                                                 
  ├─────────────────┼───────────────────┼────────────────────┤                                                                                                                                               
  │ @DataJpaTest    │ JPA 레이어만      │ Repository         │                                                                                                                                                 
  ├─────────────────┼───────────────────┼────────────────────┤                                                                                                                                                 
  │ @ServiceTest    │ (없음, 직접 Mock) │ Service            │                                                                                                                                                 
  └─────────────────┴───────────────────┴────────────────────┘

  검색 키워드: Spring Test Slicing, @WebMvcTest vs @SpringBootTest
                                                                                                                                                                                                               
  ---                                                                                                                                                                                                        
    2. Spring Application Context 로딩 원리

  @SpringBootApplication 안에 @ComponentScan이 포함되어 있고, 이게 패키지 하위의 모든 빈을 스캔합니다. 테스트 어노테이션마다 어떤 빈을 스캔하고 제외하는지 필터 조건이 다릅니다.

  검색 키워드: @SpringBootApplication 내부 구조, @ComponentScan 필터, Spring Auto-configuration
                                          
  ---                                                                                                                                                                                                          
    3. Mockito + MockMvc

    - @MockitoBean - 실제 빈 대신 Mock 객체를 컨텍스트에 등록
    - given().willReturn() - 메서드 호출 시 반환값 지정
    - willThrow().given() - void 메서드에서 예외 던지기
    - MockMvc.perform() - 실제 HTTP 요청 없이 Controller 호출
    - jsonPath() - 응답 JSON 필드 검증

  검색 키워드: Mockito BDDMockito, MockMvc 사용법, jsonPath 문법
                                                                                                                                                                                                               
  ---                                                                                                                                                                                                          
    4. JPA Auditing

  엔티티의 생성/수정 시각을 자동으로 관리하는 기능.

  @EnableJpaAuditing  → 기능 활성화                                                                                                                                                                            
  @EntityListeners(AuditingEntityListener.class)  → 엔티티에 리스너 등록
  @CreatedDate / @LastModifiedDate  → 자동으로 값 채워짐

  검색 키워드: Spring Data JPA Auditing, @CreatedDate @LastModifiedDate
                                                                                                                                                                                                               
  ---                                                                                                                                                                                                          
    5. @Configuration 분리 패턴

  설정을 목적별로 분리하는 것은 스프링의 기본 설계 원칙입니다.

    - @SpringBootApplication에 모든 걸 몰아넣으면 테스트 충돌, 가독성 저하
    - 관심사 분리(Separation of Concerns) 원칙에 따라 설정도 역할별로 클래스를 나눔

  검색 키워드: Spring @Configuration 분리, Spring 설정 클래스 분리 패턴
                                                                                                                                                                                                               
  ---                                                                                                                                                                                                          
  공부 순서 추천

  Mockito 기초                                                                                                                                                                                                 
  ↓                                                                                                                                                                                                      
  MockMvc + @WebMvcTest                                                                                                                                                                                        
  ↓                                       
  @DataJpaTest + TestEntityManager                                                                                                                                                                             
  ↓                                                                                                                                                                                                        
  Spring Context 로딩 원리                                                                                                                                                                                     
  ↓                                                                                                                                                                                                        
  @SpringBootTest 통합 테스트

  테스트 코드를 잘 짜려면 결국 "Spring이 어떤 빈을 언제 로드하는가" 를 이해하는 게 핵심입니다.                                                                                                                 
                                                                                              