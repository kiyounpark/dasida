# DASIDA Backend

DASIDA는 학습 메모를 기반으로 복습 타이밍을 계산하고, AI가 생성한 퀴즈를 푸시 알림으로 전달해 주는 PWA의 백엔드입니다. "기록만 해두면, 적절한 타이밍에 짧은 퀴즈로 복습을 끝낼 수 있는 구조"를 목표로 설계되었습니다.

## 이 repository가 제공하는 기능
- Spring Boot 3 기반의 REST API 서버
- 학습 메모 → 퀴즈 생성 파이프라인 (Spring AI + OpenAI)
- 복습 시점 계산 및 리마인더 스케줄링
- Firebase FCM 푸시 인프라와 디바이스 토큰 관리
- OAuth2 로그인, 리멤버 미, CSRF 해제 등 PWA 친화적인 인증 설정
- 5xx 중심의 Slack 장애 알림, Actuator 기반 헬스체크

## 아키텍처 한눈에 보기
```
Client (PWA)
 ├─ React + Vite
 ├─ React Query (API 상태 관리)
 ├─ Service Worker (캐싱 · 오프라인 지원)
 └─ Firebase FCM (푸시 알림 수신)

Backend (이 저장소)
 ├─ Spring Boot 3 (Java 21)
 ├─ Spring Data JPA (MySQL/H2), Query 계층 Mapper
 ├─ Spring Security + OAuth2 Client (구글 로그인)
 ├─ Spring AI ChatClient → OpenAI 연동으로 퀴즈 생성
 ├─ Reminder Scheduler (스케줄 계산 및 발송 트리거)
 ├─ Firebase Admin SDK (FCM 발송), 디바이스 토큰 관리
 ├─ AWS S3 클라이언트 기반 이미지 저장
 └─ Slack 알림 + Actuator 헬스체크
```

## 도메인 기능
- **Knowledge 관리**: 사용자가 기록한 학습 메모(텍스트·이미지)를 저장하고, 이미지 리소스를 추출해 퀴즈 생성에 활용합니다. (`com.bonju.review.knowledge`)
- **퀴즈 자동 생성**: OpenAI ChatClient를 통해 빈칸 채우기 형식의 퀴즈 JSON을 생성하고, JPA로 영속화합니다. (`com.bonju.review.quiz`)
- **리마인더 스케줄링**: 리마인더 시점을 계산하고, 예약된 푸시를 발송합니다. (`com.bonju.review.notification`)
- **푸시 인프라**: Firebase Admin SDK로 FCM 메시지를 전송하고, 만료된 디바이스 토큰을 정리합니다. (`com.bonju.review.devicetoken`, `com.bonju.review.notification`)
- **오답·정답 기록**: 사용자 풀이 기록과 오답 노트를 저장해 후속 퀴즈 추천에 활용할 수 있는 구조를 제공합니다. (`com.bonju.review.useranswer`, `com.bonju.review.wronganswernote`)
- **운영 편의**: Slack 에러 메시지 포맷터와 테스트 엔드포인트, `/health` 헬스체크, Swagger UI를 제공합니다. (`com.bonju.review.slack`, `com.bonju.review.healthcheck`)

## 기술 스택
- Java 21, Spring Boot 3.4.1
- Spring Data JPA (MySQL 기본, H2 인메모리 지원)
- Spring Security, OAuth2 Client, Remember-Me
- Spring AI ChatClient (OpenAI)
- Firebase Admin SDK (FCM), AWS SDK v2 (S3)
- Lombok, MapStruct, Springdoc OpenAPI

## 시작하기.
1. **필수 환경 변수/설정**
   - `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`: MySQL 연결 정보.
   - `spring.ai.openai.api-key`: OpenAI API Key (Spring AI 기본 키 사용).
   - `firebase.credentials`: 서비스 계정 JSON 파일 경로. (`classpath:` 또는 파일 경로)
   - `cloud.aws.region.static`, `cloud.aws.s3.bucket`: 이미지 업로드용 S3 설정.
2. **로컬 실행**
   ```bash
   ./gradlew bootRun
   ```
   - 기본 프로파일이 없다면 H2 인메모리 DB로 실행됩니다. MySQL을 사용하려면 위의 데이터소스 속성을 지정해 주세요.
3. **API 문서**
   - 애플리케이션을 띄운 후 `/swagger-ui/index.html`에서 Swagger UI를 확인할 수 있습니다.
4. **테스트**
   ```bash
   ./gradlew test
   ```

## 폴더 구조
```
com/bonju/review
 ├─ config           # 보안, CORS, Swagger, FCM 설정
 ├─ knowledge        # 학습 메모 CRUD, 내용/이미지 추출
 ├─ quiz             # 퀴즈 생성/조회/저장, OpenAI 클라이언트
 ├─ notification     # 리마인더 스케줄링 및 발송
 ├─ devicetoken      # FCM 토큰 저장/검증
 ├─ user             # OAuth2 로그인, 사용자 도메인
 ├─ useranswer       # 풀이 기록 관리
 ├─ wronganswernote  # 오답 노트 관리
 ├─ image            # 이미지 업로드/변환, S3 스토리지
 ├─ slack            # 5xx 알림 포맷터 및 테스트 엔드포인트
 ├─ healthcheck      # 헬스체크 API
 └─ util             # 공용 DTO, enum, 유틸리티
```

## 운영 노트
- 5xx 오류를 Slack으로 전송해 장애에 집중할 수 있도록 설계되었습니다.
- 퀴즈 생성 프롬프트는 `OpenAiConfig`에서 기본 System 메시지로 관리되며, 모델/프롬프트 변경 시에도 API 스펙을 유지할 수 있습니다.
- 푸시 실패 응답을 활용해 만료된 디바이스 토큰을 정리하고, 재시도 비용을 줄입니다.
