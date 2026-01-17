# 다시다(Dasida) 코딩 원칙

이 문서는 다시다 프로젝트의 모든 코드 작성 시 적용되는 원칙입니다.

상세한 내용은 `~/.claude/coding-principles.md`를 참조하세요.

## 핵심 원칙 요약

### 1. 테스트
- 하나의 테스트 = 하나의 개념
- 시간을 예측 가능하게 제어 (Clock 사용)

### 2. 코드 스타일
- Early Return 패턴 사용
- Magic Number/String 제거 → 상수화

### 3. 네이밍
- 단수/복수 정확히 구분
- 축약어 사용 금지 (명확한 변수명)

### 4. 메서드
- 하나의 메서드 = 하나의 역할
- 복잡한 로직은 메서드 추출

### 5. 객체지향
- Getter/Setter 남용 지양 (Tell, Don't Ask)
- 불변 객체 선호 (final 활용)

### 6. 리팩토링 순서
1. 중복 제거
2. 메서드 추출
3. 객체 분리
4. 도메인 추상화

### 7. Spring 테스트
- 적절한 테스트 어노테이션 사용
- `@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest` 등

### 8. 로깅
- System.out 대신 Slf4j Logger 사용
- 적절한 로그 레벨 (debug, info, warn, error)

### 9. 주석 ⭐
- **모든 public 메서드/클래스에 JavaDoc/JSDoc 작성**
- "무엇을"이 아닌 "왜"를 설명
- `@param`, `@return`, `@throws` 명시

### 10. 부정 조건
- 부정 조건은 긍정 메서드로 추출
- `!isAllowed()` → `isBlocked()` 또는 `isRateLimitExceeded()`

---

**참고**: TDD(테스트 주도 개발)는 이 원칙에서 제외됩니다.
