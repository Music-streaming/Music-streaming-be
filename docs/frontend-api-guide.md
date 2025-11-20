# 프런트엔드 연동 가이드

프런트 개발자가 Swagger 문서와 JWT 인증 흐름을 이해하고 바로 연동할 수 있도록 정리한 문서입니다. 모든 예시는 로컬에서 백엔드가 `http://localhost:8080` 으로 실행된다는 가정입니다.

---

## 1. 백엔드/Swagger 실행 방법

1. **사전 조건**
   - `src/main/resources/application.properties` 의 `spring.datasource.url` 이 현재 PC에서 접근 가능한 MySQL 주소인지 확인합니다.
   - MySQL 서비스가 실행 중이며 `music_db` 스키마, `root` 계정 비밀번호가 설정 파일과 동일해야 합니다.
2. **실행**
   - 터미널(WSL)에서 `./gradlew bootRun` 실행.
   - 서버가 성공적으로 뜨면 Tomcat 8080 포트가 열리고, 콘솔 맨 끝에 `Started MusicStreamingApplication` 로그가 표시됩니다.
3. **Swagger UI 위치**
   - 브라우저로 `http://localhost:8080/swagger-ui/index.html` 접속.
   - OpenAPI JSON은 `http://localhost:8080/v3/api-docs` 에서 확인 가능합니다.

> ✅ 보안 설정상 Swagger 관련 경로(`/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**`)는 인증 없이 접근할 수 있도록 열려 있습니다.

---

## 2. JWT 인증 흐름 요약

1. **회원가입 (`POST /api/auth/register`)**
   - Body 예시:
     ```json
     {
       "email": "user@example.com",
       "password": "Passw0rd!",
       "username": "musiclover"
     }
     ```
   - 성공 시 `201 Created` 와 `{"message": "회원가입이 완료되었습니다."}` 반환.

2. **로그인 (`POST /api/auth/login`)**
   - Body:
     ```jsonㅌ
     {
       "email": "user@example.com",
       "password": "Passw0rd!"
     }
     ```
   - 성공 시 `200 OK` 와 아래 구조를 반환.
     ```json
     {
       "token": "<JWT>",
       "username": "musiclover",
       "email": "user@example.com"
     }
     ```
   - 토큰 만료 시간은 1시간(`JwtTokenProvider.tokenValidTime`)이며 Refresh 토큰은 없습니다. 만료 시 재로그인 해야 합니다.

3. **헤더 규칙**
   - 인증이 필요한 요청에는 반드시 `Authorization: Bearer <JWT>` 헤더를 포함합니다.
   - 미인증 허용 경로: `/api/auth/**`, `/api/spotify/**`, `/api/youtube/**`, Swagger 경로. 그 외 모든 `/api/**` 요청은 인증 필요.

4. **로그아웃 (`POST /api/auth/logout`)**
   - 서버 측 상태 변화는 없으며 `{ "message": "로그아웃 완료(클라이언트에서 토큰 삭제하세요)" }` 를 리턴합니다.
   - 프런트에서는 토큰을 로컬 저장소에서 직접 제거하면 됩니다.

5. **내 정보 조회 (`GET /api/users/me`)**
   - Header에 JWT를 넣고 호출하면 `{ "id": 1, "email": "...", "username": "..." }` 형태로 반환됩니다.

---

## 3. Swagger UI에서 인증 입력하기

1. 상단의 **Authorize** 버튼 클릭.
2. Value 입력란에 `Bearer <JWT>` 전체 문자열을 붙여 넣습니다.
   - 예: `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
3. `Authorize` → `Close` 를 누르면 Swagger가 모든 API 호출 시 자동으로 헤더를 넣어 줍니다.
4. 토큰을 교체하거나 제거하려면 다시 `Authorize` 버튼을 눌러 `Logout` 을 선택합니다.

---

## 4. 통합 재생 API

### 4.1 스포티파이 + 유튜브 검색 (`GET /api/player/search?query=`)
- 설명: 스포티파이 검색 결과 10건을 불러오고, 각 곡을 YouTube API 로 매칭한 뒤 아래 필드를 반환합니다.
- 응답 필드
  - `spotifyTrackId`, `title`, `artist`, `album`, `thumbnailUrl`, `durationMs`
  - `youtubeVideoId`, `youtubeEmbedUrl`, `youtubeWatchUrl` (YouTube 검색 실패 시 null)
- 사용 예시
  ```bash
  curl -H "Authorization: Bearer <JWT>" \
       "http://localhost:8080/api/player/search?query=iu%20love%20poem"
  ```

### 4.2 단일 트랙 조회 (`GET /api/player/tracks/{spotifyTrackId}`)
- Spotify trackId 로 바로 매칭 결과를 반환합니다.
- 백엔드가 내부적으로 YouTube 검색 결과를 캐싱하므로 동일 곡 요청 시 반복 호출 부담이 적습니다.

### 4.3 플레이리스트 재생 큐 (`GET /api/player/playlists/{playlistId}`)
- 내가 소유한 플레이리스트를 재생 큐 형태로 내려줍니다.
- 응답 구조
  ```json
  {
    "playlistId": 1,
    "name": "Morning Mix",
    "description": "득근 플레이리스트",
    "tracks": [ { ...PlayableTrackResponse }, ... ]
  }
  ```
- 반환된 `tracks` 배열을 그대로 프런트의 큐 데이터로 사용하면 되고, 각 항목의 `youtubeEmbedUrl` 을 IFrame Player API 로 넘겨 재생/일시정지를 구현하면 됩니다.

> ⚠️ `/api/player/**` 는 인증이 필요한 엔드포인트입니다. Swagger 에서 호출할 때도 먼저 `Authorize` 로 JWT 를 등록해야 합니다.

## 5. 프런트엔드 연동 시나리오

1. **최초 진입**
   - 로그인 여부를 확인하기 전까지는 `/api/auth/login`, `/api/auth/register`, `/api/spotify/**`, `/api/youtube/**` 만 호출 가능합니다.

2. **회원가입/로그인 흐름**
   - 회원가입 성공 후 별도 자동 로그인은 없습니다 → 바로 로그인 API 호출 필요.
   - 로그인 성공 시 응답의 `token` 값을 안전한 저장소(localStorage, secure cookie 등)에 보관.

3. **API 호출 템플릿**
   ```ts
   const token = localStorage.getItem("jwt");
   const res = await fetch("http://localhost:8080/api/playlists/me", {
     headers: {
       "Content-Type": "application/json",
       Authorization: `Bearer ${token}`,
     },
   });
   ```

4. **토큰 만료 처리**
   - 401 응답/`{"error":"JWT 검증 실패"}` 등 메시지를 받으면 토큰 만료로 간주하고 로그인 화면으로 리다이렉트 후 토큰 삭제.

5. **Swagger 기반 스펙 확인**
   - `/swagger-ui/index.html` 에서 각 API 명세, DTO 구조, 샘플 응답을 확인 후 프런트 타입 정의를 업데이트합니다.
   - 보다 상세한 수동 테스트 체크리스트는 `docs/api-test-template.md` 를 참고하세요.

---

## 6. 자주 묻는 질문

| 질문 | 답변 |
| --- | --- |
| 토큰이 언제 만료되나요? | 발급 시점으로부터 1시간 후 만료됩니다. Refresh 토큰은 아직 없습니다. |
| Swagger 호출 시 인증이 필요한가요? | Swagger 페이지는 인증 없이 열리지만, 보호된 API를 호출하려면 반드시 `Authorize` 절차를 거쳐야 합니다. |
| 프런트에서 로그아웃은 어떻게 처리하나요? | `POST /api/auth/logout` 응답을 받은 뒤 로컬에 저장된 JWT를 삭제하면 됩니다(서버 상태 유지). |
| 외부 검색 API는 인증이 필요한가요? | `GET /api/spotify/**`, `GET /api/youtube/**` 는 자유롭게 호출 가능합니다. 나머지 플레이리스트/댓글/좋아요 등의 API는 모두 JWT 필요. |

필요한 내용이 더 있다면 이 문서에 섹션을 추가하거나, Swagger 문서에 Tag/Description 을 보강해 주세요.
