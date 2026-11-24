# FoodReview

방문 인증을 기반으로 맛집·카페 리뷰를 작성하고 관리하는 웹 서비스입니다.

## 1. 프로젝트 소개

FoodReview는 실제 방문 인증을 승인받은 사용자만 리뷰를 작성할 수 있도록 설계한 개인 프로젝트입니다.

사용자가 영수증 이미지를 첨부하여 방문 인증을 신청하면 관리자가 승인 또는 거절하며, 승인된 방문에 대해서만 리뷰를 작성할 수 있습니다.

## 2. 기술 스택

- Language: Java 17
- Framework: Spring Boot, Spring MVC
- Database: MySQL 8
- Persistence: MyBatis
- Template Engine: Thymeleaf
- Build Tool: Gradle
- Frontend: HTML, CSS, JavaScript
- Test: JUnit 5, Mockito
- Version Control: Git, GitHub

## 3. 주요 기능

### 회원
- 회원가입 및 로그인·로그아웃
- BCrypt 기반 비밀번호 해싱
- 회원정보 수정
- 세션 기반 로그인 관리

### 방문 인증
- 방문 인증 신청 및 영수증 이미지 업로드
- 관리자 승인·거절
- 방문 인증 상태 관리 (대기, 승인, 거절)
- 본인의 방문 인증 내역 및 리뷰 작성 여부 조회

### 리뷰
- 승인된 본인 방문에 대한 리뷰 작성
- 리뷰 목록 및 상세 조회
- 키워드 검색 및 별점 필터
- 페이징 (페이지당 10개)
- 본인 리뷰 수정·삭제
- 중복 리뷰 작성 방지

### 관리자
- 방문 인증 신청 목록 조회
- 방문 인증 승인·거절
- 회원 목록 조회 및 활성 상태 관리

### 이미지 파일 관리
- 파일 크기 5MB 제한
- 이미지 확장자 및 파일 헤더 검증
- UUID 기반 파일명 생성
- 리뷰 이미지 교체·삭제 시 기존 파일 정리
- DB 등록 실패 시 저장된 이미지 삭제 처리

## 4. REST API

| Method | URL | 설명 | 권한 |
| --- | --- | --- | --- |
| GET | /api/reviews | 리뷰 검색 및 페이징 조회 | 전체 |
| POST | /api/reviews | 리뷰 등록 | 로그인 회원 |
| PATCH | /api/admin/visits/{vno}/approve | 방문 인증 승인 | 관리자 |
| PATCH | /api/admin/visits/{vno}/reject | 방문 인증 거절 | 관리자 |

### 리뷰 조회 요청 예시

GET /api/reviews?keyword=행복카페&rating=5&page=1

- keyword: 검색어
- rating: 별점 필터 (1~5)
- page: 페이지 번호

### 주요 HTTP 상태 코드

- 200: 요청 성공
- 201: 리뷰 등록 성공
- 400: 잘못된 요청
- 401: 인증 필요
- 403: 접근 권한 없음
- 409: 중복 리뷰 또는 이미 처리된 방문 인증

## 5. 인증·인가 및 데이터 정합성

- 세션 기반 사용자 인증
- LoginInterceptor를 통한 로그인 및 회원 활성 상태 검사
- AdminInterceptor를 통한 관리자 접근 제한
- 회원 번호와 방문 승인 상태를 기반으로 리뷰 작성 자격 검증
- 리뷰 수정·삭제 시 소유권 검증
- review.vno UNIQUE 제약조건을 통한 중복 리뷰 등록 방지
- 조건부 UPDATE를 통한 중복 승인·거절 방지

방문 인증 상태값은 다음과 같습니다.

| 상태값 | 의미 |
| --- | --- |
| 0 | 승인 대기 |
| 1 | 승인 |
| 2 | 거절 |

## 6. 데이터베이스

| 테이블 | 설명 |
| --- | --- |
| member | 회원 정보 |
| place | 맛집·카페 정보 |
| visit | 방문 인증 정보 |
| review | 리뷰 정보 |

회원, 가게, 방문 인증, 리뷰 간의 관계를 외래키로 관리합니다.

초기 테이블 생성 SQL은 database/schema.sql에 포함되어 있습니다.

ERD는 추후 추가할 예정입니다.

## 7. 테스트 및 검증

### 자동화 테스트
- 관리자 방문 인증 승인·거절 성공 및 실패 테스트
- 리뷰 REST API 요청 및 응답 테스트
- 로그인 인터셉터 테스트

### 실제 MySQL 연동 검증
- 리뷰 등록 및 DB 저장 확인
- 중복 리뷰 등록 시 409 응답 및 중복 저장 방지 확인
- 리뷰 페이징 조회
- 키워드 검색 및 별점 필터
- 검색어와 별점 복합 필터
- 신규 데이터베이스에서 테이블 생성 스크립트 실행 확인

테스트 실행 명령어:

```bash
bash gradlew test
```

## 8. 실행 방법

### 8-1. 실행 환경

- Java 17
- MySQL 8
- Git

### 8-2. 프로젝트 다운로드

```bash
git clone https://github.com/ahyun00/foodreview.git
cd foodreview
```

비공개 저장소인 경우 접근 권한이 필요합니다.

### 8-3. 데이터베이스 생성

MySQL에 연결한 후 database/schema.sql을 실행합니다.

신규 데이터베이스 초기화용 스크립트이며, 기존 테이블이 존재하는 환경에서는 중복 실행하지 않습니다.

### 8-4. 데이터베이스 연결 설정

src/main/resources/application.properties 파일을 생성하고 아래와 같이 설정합니다.

```properties
spring.application.name=foodreview

spring.datasource.url=jdbc:mysql://localhost:3306/foodreview
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

mybatis.mapper-locations=classpath:mapper/**/*.xml
mybatis.configuration.map-underscore-to-camel-case=true

spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=10MB
```

YOUR_DB_USERNAME과 YOUR_DB_PASSWORD는 각자의 MySQL 접속 정보로 변경합니다.

실제 DB 접속 정보는 GitHub에 포함하지 않습니다.

### 8-5. 애플리케이션 실행

```bash
bash gradlew bootRun
```

브라우저에서 아래 주소로 접속합니다.

http://localhost:8080

### 8-6. 관리자 계정 설정

로컬 테스트 환경에서 회원가입 후 해당 계정의 권한을 ADMIN으로 변경합니다.

```sql
UPDATE foodreview.member
SET role = 'ADMIN'
WHERE email = 'admin@example.com';
```

이메일은 직접 가입한 테스트 계정의 이메일로 변경합니다.

권한 변경 후 다시 로그인합니다.

### 8-7. 가게 테스트 데이터 등록

방문 인증 기능을 이용하기 위해 예시 가게 데이터를 등록합니다.

```sql
INSERT INTO foodreview.place
    (name, category, address, phone, description)
VALUES
    ('테스트카페', '카페', '테스트 주소',
     '000-0000-0000', '기능 검증용 가게');
```

### 8-8. 이미지 저장 경로

- 영수증 이미지: uploads/receipts/
- 리뷰 이미지: uploads/reviews/

이미지는 애플리케이션 실행 디렉터리를 기준으로 로컬 파일 시스템에 저장됩니다.

업로드된 사용자 이미지는 GitHub에 포함하지 않습니다.

## 9. 향후 개선 계획

- 서버 배포 및 배포 환경 검증
- ERD 및 프로젝트 문서 보완
- 테스트 범위 확대