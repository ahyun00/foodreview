
# FoodReview

방문 인증을 기반으로 맛집·카페 리뷰를 작성하고 관리하는 웹 서비스입니다.

## 1. 프로젝트 소개

FoodReview는 실제 방문을 인증한 사용자만 리뷰를 작성할 수 있도록 설계한 프로젝트입니다.

사용자가 방문 인증을 신청하면 관리자가 승인 또는 거절하며, 승인된 방문에 대해서만 리뷰를 작성할 수 있습니다.

## 2. 기술 스택

- Language: Java 17
- Framework: Spring Boot, Spring MVC
- Database: MySQL 8
- Persistence: MyBatis
- Template Engine: Thymeleaf
- Build Tool: Gradle
- Frontend: HTML, CSS, JavaScript

## 3. 주요 기능

### 회원
- 회원가입 및 로그인·로그아웃
- 회원정보 수정
- 세션 기반 로그인 관리

### 방문 인증
- 방문 인증 신청 및 영수증 이미지 업로드
- 관리자 승인·거절
- 방문 인증 상태 관리

### 리뷰
- 승인된 방문에 대한 리뷰 작성
- 리뷰 목록 및 상세 조회
- 본인 리뷰 수정·삭제
- 중복 리뷰 작성 방지

### 관리자
- 방문 인증 신청 관리
- 회원 관리

## 4. 데이터베이스

주요 테이블은 다음과 같습니다.

- member: 회원 정보
- place: 맛집·카페 정보
- visit: 방문 인증 정보
- review: 리뷰 정보

ERD는 추후 추가할 예정입니다.

## 5. 실행 환경

- Java 17
- MySQL 8
- Gradle

실행 시 별도의 데이터베이스 연결 설정이 필요합니다.

## 6. 향후 개선 계획

- REST API 추가 및 검증
- 핵심 비즈니스 로직 테스트 보완
- 배포 및 문서 정리
