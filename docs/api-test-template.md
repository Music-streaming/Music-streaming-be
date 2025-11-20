# API 테스트 양식

로컬 개발 환경에서 `http://localhost:8080` 기준으로 API 동작을 빠르게 점검할 수 있는 체크리스트입니다.  
아래 형식을 그대로 복사해 Postman, curl, 혹은 다른 도구에서 테스트한 뒤, `테스트 메모` 구역에 실제 응답을 정리하면 됩니다.

---

## 공통 규칙

- **인증 필요 Ax`PI**: 사용자 정보 조회, 플레이리스트 조작, 좋아요, 만족도, 댓글 관련 요청은 `Authorization: Bearer <JWT>` 헤더가 필요합니다.
- **콘텐츠 타입**: JSON Body를 보내는 요청은 `Content-Type: application/json`을 사용합니다.
- **테스트 메모**: 각 섹션 마지막 부분에 있는 블록에 실제 응답 상태 코드, 본문, 비고를 남기세요.

---

## 1. 인증 / 사용자

### POST `/api/auth/register`
- 설명: 신규 회원 가입
- Body 예시
```json
{
  "email": "user@example.com",
  "password": "Passw0rd!",
  "username": "musiclover"
}
```
- 기대 응답: `201 Created`, `{ "message": "회원가입이 완료되었습니다." }`

테스트 메모:
```
- 상태 코드:
- 응답 본문:
- 비고:
```

### POST `/api/auth/login`
- 설명: 로그인 후 JWT 반환
- Body 예시
```json
{
  "email": "user@example.com",
  "password": "Passw0rd!"
}
```
- 기대 응답: `200 OK`, `{ "token": "...", "username": "...", "email": "..." }`

테스트 메모:
```
- 상태 코드:
- 응답 본문:
- 발급 토큰:
```

### POST `/api/auth/logout`
- 설명: 서버 측 상태 변경은 없으며 성공 메시지 반환
- 기대 응답: `200 OK`, `"로그아웃 완료(클라이언트에서 토큰 삭제하세요)"`

테스트 메모:
```
- 상태 코드:
- 응답 본문:
```

### GET `/api/users/me`
- 설명: 현재 로그인한 사용자 정보 조회
- 헤더: `Authorization: Bearer <JWT>`
- 기대 응답: `200 OK`, `{ "id": 1, "email": "...", "username": "..." }`

테스트 메모:
```
- 상태 코드:
- 응답 본문:
```

---

## 2. 플레이리스트

### POST `/api/playlists`
- 설명: 플레이리스트 생성
- Body 예시
```json
{
  "name": "My Playlist",
  "description": "기분 좋은 음악 모음",
  "public": true
}
```
- 기대 응답: `201 Created`, `PlaylistResponse`

테스트 메모:
```
- 상태 코드:
- 응답 본문:
```

### GET `/api/playlists/me`
- 설명: 내 플레이리스트 전체 조회
- 기대 응답: `200 OK`, `[ PlaylistResponse, ... ]`

테스트 메모:
```
- 상태 코드:
- 응답 본문 개수:
```

### GET `/api/playlists/{id}`
- 설명: 특정 플레이리스트 상세
- 기대 응답: `200 OK`, `PlaylistResponse`

테스트 메모:
```
- 상태 코드:
- 응답 본문:
```

### POST `/api/playlists/{id}/tracks`
- 설명: 플레이리스트에 트랙 추가 (사전 단계로 Spotify 검색 또는 자체 트랙 ID 확보 필요)
- Body 예시
```json
{
  "trackId": "spotify-track-id",
  "title": "곡 제목 (선택)",
  "artist": "아티스트명 (선택)",
  "albumArtUrl": "썸네일 URL (선택)"
}
```
- 체크리스트
  1. `/api/spotify/search?query=keyword` 호출로 `trackId` 확보
  2. 위 Body로 플레이리스트에 추가
  3. `/api/playlists/{id}` 재호출 시 `tracks` 목록에 새 항목 존재 여부 확인
- 기대 응답: `200 OK`, 갱신된 `PlaylistResponse`

테스트 메모:
```
- 검색 키워드: 저스틴비버
- trackId / title: 4iJyoBOLtHqaGxP12qzhQI/peaches
- 상태 코드:
- 응답 본문:
```

### DELETE `/api/playlists/{id}/tracks/{trackId}`
- 설명: 플레이리스트에서 특정 트랙 제거
- 체크리스트
  1. 삭제 전에 `/api/playlists/{id}`로 대상 trackId 존재 여부 확인
  2. DELETE 호출
  3. 다시 GET `/api/playlists/{id}`로 제거 확인
- 기대 응답: `204 No Content`

테스트 메모:
```
- 상태 코드:
- 사전/사후 트랙 개수:
- 추가 확인 사항:
```

---

## 3. 트랙 좋아요 & 만족도

### POST `/api/tracks/{trackId}/like`
- 설명: 좋아요 토글 (누르면 좋아요/취소)
- 기대 응답: `200 OK`, `true | false` (현재 상태)

테스트 메모:
```
- 상태 코드:
- 응답 본문:
- DB 반영 여부:
```

### GET `/api/tracks/{trackId}/like/count`
- 설명: 전체 좋아요 수 조회
- 기대 응답: `200 OK`, 숫자

테스트 메모:
```
- 상태 코드:
- 응답 본문:
```

### GET `/api/tracks/{trackId}/like/status`
- 설명: 내 좋아요 여부
- 기대 응답: `200 OK`, `true | false`

테스트 메모:
```
- 상태 코드:
- 응답 본문:
```

### PUT `/api/tracks/{trackId}/satisfaction`
- 설명: 만족/불만족 선택
- Body 예시
```json
{
  "type": "SATISFIED"
}
```
- 기대 응답: `200 OK`

테스트 메모:
```
- 상태 코드:
- 응답 본문:
```

### GET `/api/tracks/{trackId}/satisfaction/me`
- 설명: 내가 선택한 만족도 조회
- 기대 응답: `200 OK`, `{ "type": "SATISFIED" | "DISSATISFIED" | null }`

테스트 메모:
```
- 상태 코드:
- 응답 본문:
```

### GET `/api/tracks/{trackId}/satisfaction/summary`
- 설명: 전체 만족/불만족 집계
- 기대 응답: `200 OK`, `{ "satisfiedCount": 0, "dissatisfiedCount": 0 }`

테스트 메모:
```
- 상태 코드:
- 응답 본문:
```

---

## 4. 댓글 (플레이리스트 트랙 단위)

### POST `/api/playlist-tracks/{playlistTrackId}/comments`
- 설명: 댓글/대댓글 등록 (`playlistTrackId`는 앞선 플레이리스트 트랙 추가 시 응답에서 확인 가능)
- Body 예시
```json
{
  "content": "이 곡 너무 좋아요!",
  "parentId": null
}
```
- 체크리스트
  1. `/api/playlists/{playlistId}`에서 `playlistTrackId` 찾기
  2. 댓글 작성 후 응답의 `id` 기록
  3. `/api/playlist-tracks/{playlistTrackId}/comments` 호출 시 방금 댓글이 포함되는지 확인
- 기대 응답: `200 OK`, `CommentResponse`

테스트 메모:
```
- playlistTrackId:
- 상태 코드:
- 응답 본문:
```

### GET `/api/playlist-tracks/{playlistTrackId}/comments`
- 설명: 특정 트랙 댓글 목록
- 기대 응답: `200 OK`, `[ CommentResponse, ... ]`
- 비고: 대댓글은 `children` 필드로 중첩되어 내려오는지 확인

테스트 메모:
```
- 상태 코드:
- 응답 본문 개수:
- 중첩 구조 확인:
```

### DELETE `/api/playlist-tracks/{playlistTrackId}/comments/{commentId}`
- 설명: 자신의 댓글 삭제
- 체크리스트: 삭제 후 GET 목록에서 사라졌는지 확인
- 기대 응답: `200 OK`, `"삭제 완료"`

테스트 메모:
```
- 상태 코드:
- 후속 검증:
```

---

## 5. 외부 검색

### GET `/api/spotify/search?query={keyword}`
- 설명: Spotify 트랙 검색 프록시
- 기대 응답: `200 OK`, Spotify API 응답 JSON

테스트 메모:
```
- 상태 코드:
- 응답 본문 요약:
```

### GET `/api/youtube/search?query={keyword}`
- 설명: 유튜브 영상 ID 검색
- 기대 응답: `200 OK`, `{ "videoId": "...", "embedUrl": "https://www.youtube.com/embed/..." }` (검색 실패 시 `404`)

테스트 메모:
```
- 상태 코드:
- 응답 본문:
```

---

필요 시 이 파일을 복제해 실제 테스트 결과를 남기거나, 항목별로 체크박스를 추가해 QA 체크리스트로 활용하세요.
