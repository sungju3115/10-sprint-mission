# 요구사항

## 기본

## 데이터베이스

- [x] 아래와 같이 데이터베이스 환경을 설정하세요.

* 데이터베이스: discodeit
* 유저: discodeit_user
* 패스워드: discodeit1234

- [x] ERD를 참고하여 DDL을 작성하고, 테이블을 생성하세요.

작성한 DDL 파일은 `/src/main/resources/schema.sql` 경로에 포함하세요.

PK: Primary Key
UK: Unique Key
NN: Not Null
FK: Foreign Key

ON DELETE CASCADE: 연관 엔티티 삭제 시 같이 삭제
ON DELETE SET NULL: 연관 엔티티 삭제 시 NULL로 변경

---

## Spring Data JPA 적용하기

- [x] Spring Data JPA와 PostgreSQL을 위한 의존성을 추가하세요.

- [x] 앞서 구성한 데이터베이스에 연결하기 위한 설정값을 `application.yaml` 파일에 작성하세요.

- [x] 디버깅을 위해 SQL 로그와 관련된 설정값을 `application.yaml` 파일에 작성하세요.

---

## 엔티티 정의하기

- [x] 클래스 다이어그램을 참고해 도메인 모델의 공통 속성을 추상 클래스로 정의하고 상속 관계를 구현하세요.
이때 Serializable 인터페이스는 제외합니다.

패키지명: `com.sprint.mission.discodeit.entity.base`

- [x] JPA의 어노테이션을 활용해 createdAt, updatedAt 속성이 자동으로 설정되도록 구현하세요.

* `@CreatedDate`
* `@LastModifiedDate`

- [x] 클래스 다이어그램을 참고해 클래스 참조 관계를 수정하세요.
- [x] 필요한 경우 생성자, update 메소드를 수정할 수 있습니다.
단, 아직 JPA Entity와 관련된 어노테이션은 작성하지 마세요.

---

## 클래스 다이어그램

화살표의 방향과 화살표 유무에 유의하세요.

---

## ERD와 클래스 다이어그램을 토대로 연관관계 매핑 정보를 표로 정리해보세요.

| 엔티티 관계                              | 다중성 | 방향성                                     | 부모-자식 관계                                   | 연관관계의 주인           |
| ----------------------------------- | --- | --------------------------------------- | ------------------------------------------ | ------------------ |
| Users : BinaryContents              | 1:1 | Users → BinaryContents 단방향              | 부모: BinaryContents, 자식: Users              | Users              |
| UserStatuses : Users                | 1:1 | UserStatuses → Users 단방향                | 부모: Users , 자식: UserStatuses               | UserStatuses       |
| Messages : Users                    | N:1 | Messages → Users 단방향                    | 부모: Users, 자식: Messages                    | Messages           |
| Messages : Channels                 | N:1 | Messages → Channels 단방향                 | 부모: Channels, 자식: Messages                 | Messages           |
| Messages : MessageAttachments       | 1:N | MessageAttachments → Messages 단방향       | 부모: Messages, 자식: MessageAttachments       | MessageAttachments |
| BinaryContents : MessageAttachments | 1:N | MessageAttachments → BinaryContents 단방향 | 부모: BinaryContents, 자식: MessageAttachments | MessageAttachments |
| ReadStatuses : Users                | N:1 | ReadStatuses → Users 단방향                | 부모: Users, 자식: Messages                    | Messages           |
| ReadStatuses : Channels             | N:1 | ReadStatuses → Channels 단방향             | 부모: Channels, 자식: Messages                 | Messages           |

---

## JPA 주요 어노테이션을 활용해 ERD, 연관관계 매핑 정보를 도메인 모델에 반영해보세요.

* `@Entity`, `@Table`
* `@Column`, `@Enumerated`
* `@OneToMany`, `@OneToOne`, `@ManyToOne`
* `@JoinColumn`, `@JoinTable`

- [x] ERD의 외래키 제약 조건과 연관관계 매핑 정보의 부모-자식 관계를 고려해 영속성 전이와 고아 객체를 정의하세요.

* cascade
* orphanRemoval

---

## 레포지토리와 서비스에 JPA 도입하기

- [x] 기존의 Repository 인터페이스를 `JpaRepository`로 정의하고 쿼리메소드로 대체하세요.

- [x] FileRepository와 JCFRepository 구현체는 삭제합니다.

- [x] 영속성 컨텍스트의 특징에 맞추어 서비스 레이어를 수정해보세요.

힌트:

* 트랜잭션
* 영속성 전이
* 변경 감지
* 지연로딩

---

## DTO 적극 도입하기

- [x] Entity를 Controller 까지 그대로 노출했을 때 발생할 수 있는 문제점에 대해 정리해보세요.
DTO를 적극 도입했을 때 보일러플레이트 코드가 많아지지만, 그럼에도 불구하고 어떤 이점이 있는지 알 수 있을거에요.
(이 내용은 PR에 첨부해주세요.)

### 문제점

1. 엔티티는 테이블의 구조를 대변한다. api와 결합하게 되면 테이블의 구조가 바뀌었을때 api의 스펙도 바뀌게 되어서 프론트엔드와의 호환에도 영향을 끼쳐서 에러를 일으키게된다.
2. 민감한 정보를 그대로 가져가게 되어서 보안상 이슈가 발생하게 된다.
3. 양방향 관계를 가진 데이터가 참조 되어있을때 데이터 호출 시 계속해서 서로를 호출하게되는 순환호출이 이루어짐으로 데이터가 넘처나게됨

### Dto 도입 이점

* 캡슐화 및 보안: 민감한 필드(password 등)를 원천 차단하고 필요한 데이터만 선택적으로 노출한다.
* 독립성 보장: 내부 DB 구조가 어떻게 바뀌든 DTO 스펙만 유지하면 외부 API 사용자에게는 아무런 영향이 없다.
* 데이터 검증(Validation): `@notblank`, `@min` 같은 검증 로직을 엔티티에 넣으면 DB 로직과 섞여 지저분해지지만, DTO에 넣으면 입력 값 검증 책임을 명확히 분리할 수 있다.

---

## 다음의 클래스 다이어그램을 참고하여 DTO를 정의하세요

- [x] Entity를 DTO로 매핑하는 로직을 책임지는 Mapper 컴포넌트를 정의해 반복되는 코드를 줄여보세요.

패키지명: `com.sprint.mission.discodeit.mapper`

---

## BinaryContent 저장 로직 고도화

데이터베이스에 이미지와 같은 파일을 저장하면 성능 상 불리한 점이 많습니다.
따라서 실제 바이너리 데이터는 별도의 공간에 저장하고, 데이터베이스에는 바이너리 데이터에 대한 메타 정보(파일명, 크기, 유형 등)만 저장하는 것이 좋습니다.

- [x] BinaryContent 엔티티는 파일의 메타 정보(`fileName`, `size`, `contentType`)만 표현하도록 `bytes` 속성을 제거하세요.

---

## BinaryContentStorage 인터페이스 설계

패키지명: `com.sprint.mission.discodeit.storage`

### BinaryContentStorage

바이너리 데이터의 저장/로드를 담당하는 컴포넌트입니다.

```
UUID put(UUID, byte[])
```

UUID 키 정보를 바탕으로 byte[] 데이터를 저장합니다.
UUID는 BinaryContent의 Id 입니다.

```
InputStream get(UUID)
```

키 정보를 바탕으로 byte[] 데이터를 읽어 InputStream 타입으로 반환합니다.
UUID는 BinaryContent의 Id 입니다.

```
ResponseEntity<?> download(BinaryContentDto)
```

HTTP API로 다운로드 기능을 제공합니다.
BinaryContentDto 정보를 바탕으로 파일을 다운로드할 수 있는 응답을 반환합니다.

---

## 서비스 레이어 리팩토링

- [x] 서비스 레이어에서 기존에 BinaryContent를 저장하던 로직을 BinaryContentStorage를 활용하도록 리팩토링하세요.

---

## BinaryContentController

- [x] 파일을 다운로드하는 API를 추가하고, BinaryContentStorage에 로직을 위임하세요.

엔드포인트
GET `/api/binaryContents/{binaryContentId}/download`

요청값: BinaryContentId
방식: Path Variable
응답: `ResponseEntity<?>`

---

## 로컬 디스크 저장 방식 구현

- [x] 로컬 디스크 저장 방식으로 BinaryContentStorage 구현체를 구현하세요.

`discodeit.storage.type` 값이 `local` 인 경우에만 Bean으로 등록되어야 합니다.

Path root
로컬 디스크의 루트 경로입니다.

- [x] `discodeit.storage.local.root-path` 설정값을 정의하고, 이 값을 통해 주입합니다.

### init()

루트 디렉토리를 초기화합니다.
Bean이 생성되면 자동으로 호출되도록 합니다.

### resolvePath(UUID)

파일의 실제 저장 위치에 대한 규칙을 정의합니다.

파일 저장 위치 규칙 예시:
`{root}/{UUID}`

put, get 메소드에서 호출해 일관된 파일 경로 규칙을 유지합니다.

### download(BinaryContentDto)

get 메소드를 통해 파일의 바이너리 데이터를 조회합니다.
BinaryContentDto와 바이너리 데이터를 활용해 ResponseEntity 응답을 생성 후 반환합니다.

---

## 페이징과 정렬

- [x] 메시지 목록을 조회할 때 다음의 조건에 따라 페이지네이션 처리를 해보세요.

* 50개씩 최근 메시지 순으로 조회합니다.
* 총 메시지가 몇개인지 알 필요는 없습니다.

---

## 페이지네이션 DTO

- [x] 일관된 페이지네이션 응답을 위해 제네릭을 활용해 DTO로 구현하세요.

패키지명: `com.sprint.mission.discodeit.dto.response`

* content: 실제 데이터입니다.
* number: 페이지 번호입니다.
* size: 페이지의 크기입니다.
* totalElements: T 데이터의 총 갯수를 의미하며, null일 수 있습니다.

---

## Page / Slice → DTO Mapper

Slice 또는 Page 객체로부터 DTO를 생성하는 Mapper를 구현하세요.

패키지명: `com.sprint.mission.discodeit.mapper`

확장성을 위해 제네릭 메소드로 구현하세요.

---

# 심화

## N+1 문제

N+1 문제가 발생하는 쿼리를 찾고 해결해보세요.

---

## 읽기전용 트랜잭션 활용

프로덕션 환경에서는 OSIV를 비활성화하는 경우가 많습니다.
이때 서비스 레이어의 조회 메소드에서 발생할 수 있는 문제를 식별하고, 읽기 전용 트랜잭션을 활용해 문제를 해결해보세요.

---

## OSIV 비활성화하기

---

# 페이지네이션 최적화

- [x] 오프셋 페이지네이션과 커서 페이지네이션 방식의 차이에 대해 정리해보세요.

### 오프셋 방식

* 데이터 전체를 조회한후 정해진 오프셋(page = 10 , size = 5)만큼 나눠서 가져온다.

### 페이지네이션 방식

* 페이지네이션은 cursor라는 기준을 세운다.
* 마치 책갈피처럼 기준을 세워서 기준에 적합한 데이터만 가져온다.
* 따라서 오프셋 방식처럼 모든 데이터를 조회해서 가져오는게 아닌 기준에 적합한 데이터만 가져온 후 그 이후의 데이터가 존재하는지만 파악한다.
* 만약 다음 데이터를 가지고 오고 싶다면 그 이후의 데이터가 있는지 파악하고 있다면 새로운 기준(cursor)을 세워서 다시 적합한 범위의 데이터만 조회해 가져온다.

기존에 구현한 오프셋 페이지네이션을 커서 페이지네이션으로 리팩토링하세요.

- [x] 다음의 API 명세를 준수하세요.
API 스펙을 준수한다면, 아래의 프론트엔드 코드와 호환됩니다.

---

# MapStruct 적용

- [x] Entity와 DTO를 매핑하는 보일러플레이트 코드를 MapStruct 라이브러리를 활용해 간소화해보세요.

---

멘토에게
- 전체적인 구현은 완료했지만, 아직 강사님 피드백까지 반영안되었습니다!, 멘토님 피드백은 틈틈히 반영하도록 하겠습니다!
- commit 메시지를 깔끔하게 작성하고 싶은데, 혹시 어떤 방식으로 작성해야할까요?
- 주석을 보기 좋게 활용하려면 어떻게 해야할까요?


