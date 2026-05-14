## Spring Security 환경설정
- [ ]  프로젝트에 Spring Security 의존성을 추가하세요.
- [ ]  Security 설정 클래스를 생성하세요.

패키지명: com.sprint.mission.discodeit.config
클래스명: SecurityConfig
- [ ]  SecurityFilterChain Bean을 선언하세요.
- [ ]  가장 기본적인 SecurityFilterChain을 등록하고, 이때 등록되는 필터 목록을 디버깅해보세요. 필터 목록은 PR에 첨부하세요.

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.build();
}
```

- [ ]  개발 환경에서 Spring Security 모듈의 로깅 레벨을 trace로 설정하세요.

각 요청마다 통과하는 필터 목록을 확인할 수 있습니다.