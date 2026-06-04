# 학습 질문 정리

## Spring Event

### BinaryContentCreatedEvent
- **Q. BinaryContentStorage.put을 직접 호출하는 대신 이벤트를 발행하는 이유가 뭔가?**
  - DB 저장 성공 확정 후 파일 저장을 보장하기 위해
  - DB 롤백 시 파일 저장 자체를 막을 수 있음

- **Q. @TransactionalEventListener에서 AFTER_COMMIT을 사용하는 이유가 뭔가?**
  - DB 커밋이 성공했을 때만 리스너가 실행되도록 보장
  - DB 롤백 시 리스너 실행 자체를 막음
  - 일반 @EventListener는 이벤트 발행 즉시 실행되어 DB 롤백 시 불일치 발생

- **Q. updateStatus할 때 binaryContent.set 이런식으로 가면 동시성 문제 안터지나?**
  - 이 케이스는 BinaryContent 하나당 이벤트 하나 → 리스너 하나 → 업데이트 한 번이라 동시성 문제 없음
  - 동시성 문제는 같은 엔티티를 두 스레드가 동시에 수정할 때 발생

- **Q. DB 쿼리문으로 업데이트하는 것이나 setter로 업데이트하는 것이나 비슷한가?**
  - 결과는 같지만 동작 방식이 다름
  - setter(Dirty Checking): SELECT → 메모리 변경 → UPDATE(전체 컬럼)
  - 쿼리 방식: UPDATE(지정 컬럼만), SELECT 불필요
  - status 하나만 바꾸는 경우 쿼리 방식이 더 효율적

- **Q. status 업데이트 트랜잭션을 강제로 새로 참여하게 하는 구조가 맞나?**
  - 맞음. AFTER_COMMIT 이후 새 트랜잭션(REQUIRES_NEW)을 열고 status 업데이트는 그 트랜잭션에 참여(REQUIRED)

- **Q. updateStatus에 트랜잭션 하는 거랑 이벤트 발행 메서드를 새로 참여하게 하는 거랑 무슨 차이?**
  - 리스너에 REQUIRES_NEW: 파일 I/O 동안 DB 커넥션을 불필요하게 점유
  - updateStatus에만 REQUIRES_NEW: 파일 I/O 후 필요할 때만 커넥션 사용 → 더 효율적

---

## JWT 인증

- **Q. JwtRegistry 설정을 안 해서 로그인이 안 되는 건가?**
  - 아님. JwtRegistry는 동시 로그인 제한, 강제 로그아웃 같은 상태 관리 기능
  - 실제 원인: JwtAuthenticationFilter가 Security 필터 체인에 등록되지 않아서 Bearer 토큰 인식 불가

- **Q. 401 ErrorResponse는 어디서 걸러야 하나?**
  - SecurityConfig의 authenticationEntryPoint에서 자동 처리
  - 컨트롤러/서비스에서 따로 처리 불필요

---

## 알림 기능

- **Q. notificationEnabled PRIVATE=true, PUBLIC=false 초기화가 무슨 뜻인가?**
  - PRIVATE: 명시적으로 참여한 채널 → 기본 알림 ON
  - PUBLIC: 누구나 볼 수 있어 채널이 많을 수 있음 → 기본 알림 OFF, 원하는 것만 직접 켜는 방식

- **Q. 이벤트 리스너에 서비스 계층이 들어가도 되나?**
  - 됨. 오히려 권장
  - 리스너는 "이벤트를 받아서 서비스에 위임"하는 역할만 하는 게 깔끔함
  - Repository 직접 접근보다 로직 재사용, 테스트 용이

- **Q. 유저가 만 명일 경우 알림 처리를 어떻게 해야 하나?**
  - 문제: 전체 메모리 로드, N+1, 단건 INSERT 반복
  - 해결: fetch join으로 N+1 해결, 페이징으로 청크 단위 처리

- **Q. 그래서 Spring Batch가 있는 건가?**
  - 맞음. 대용량 데이터를 청크 단위로 Read → Process → Write
  - 실패 시 재시도, 진행 상황 기록, 중단/재시작 가능
  - 실무에서 대용량 처리의 표준 해답

---

## 기타

- **Q. @RequiredArgsConstructor를 JPA 엔티티에 쓰면 문제가 있나?**
  - JPA는 기본 생성자(no-args constructor) 필수
  - @RequiredArgsConstructor는 final 필드 생성자만 만들어줌
  - @NoArgsConstructor(access = AccessLevel.PROTECTED)를 함께 써야 함

- **Q. findAll()에 receiverId 파라미터를 받아도 실제로 사용 안 하면?**
  - 전체 알림이 다 조회됨
  - Repository에 findAllByReceiver_Id(receiverId) 메서드 추가해서 필터링해야 함
