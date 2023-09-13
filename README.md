# Auth Service

## 개발 프레임워크

- Spring Boot
- Spring Security
- JWT
- JUnit 5

## 테이블 설계

유저 테이블 (MySQL)

| 컬럼 이름 | 데이터 타입 | 설명 |
|:--------:|:--------:|:--------|
| id | bigint(20) | Primary Key |
| name | varchar(255) | 유저 이름 |
| password | varchar(255) | 비밀번호 (BCrypt 암호화 적용) |

토큰 테이블 (Redis)

| 키 | 데이터 타입 | 설명 |
|:--------:|:--------:|:--------|
| key | String | JWT 토큰 |
| value | String | 빈 문자열 |
| expire time | Long | 토큰의 남은 유효 시간 |

## 문제해결 전략
- 회원가입: 사용자로부터 이름과 비밀번호를 받고 비밀번호는 BCrypt를 사용하여 암호화하여 저장했습니다.
- 로그인: 로그인 요청 시, 검증에 성공하면 JWT 토큰을 생성하여 헤더에 담아 응답했습니다.
  - 참고했던 Spring Security 인증 처리 과정
        ![money rain](https://velog.velcdn.com/images/gwichanlee/post/0aedf158-01c3-4cd4-99c5-acd64091fe33/image.png)
- JWT 검증: 요청 헤더의 'Authorization' 키 값에서 토큰을 가져와 검증합니다. 만약 검증에 실패하면 예외를 발생시킵니다.
- 로그아웃: 로그아웃 요청 시, 로그아웃한 사용자의 토큰 정보를 레디스에 저장하여 로그아웃을 구현하였습니다.

## 빌드 및 실행 방법

1. Docker 네트워크 생성:
```
docker network create auth_network
```
2. Docker 이미지 빌드:
```
docker-compose build
```
3. Docker 컨테이너 실행:
```
docker-compose up -d
```

## API 명세서

### 회원가입

- 엔드포인트: `/api/v1/users/register`
- 메소드: POST
- 요청 본문:
```json
{
  "name": "<사용자명>",
  "password": "<비밀번호>"
}
```
- 성공 응답: HTTP 200 (OK)
```json
{
  "access_token": "<접근_토큰>",
  "expiration": "<만료_시간>"
}
```

### 로그아웃

- 엔드포인트: `/api/v1/users/logout`
- 메소드: POST
- 헤더:
```
Authorization: Bearer <접근_토큰>
```
- 성공 응답: HTTP 204 (No Content)

### 토큰 검증

- 엔드포인트: `/api/v1/users/validate`
- 메소드: GET
- 헤더:
```
Authorization: Bearer <접근_토큰>
```
- 성공 응답: HTTP 204 (No Content)

### 로그인

- 엔드포인트: `/api/v1/users/login`
- 메소드: POST
- 요청 본문:
```json
{
  "name": "<사용자명>",
  "password": "<비밀번호>"
}
```
- 성공 응답: HTTP 200 (OK)
- 헤더:
```
Authorization: Bearer <접근_토큰>
```