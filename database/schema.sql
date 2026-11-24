CREATE DATABASE IF NOT EXISTS foodreview
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE foodreview;

-- 1. 회원 테이블
CREATE TABLE `member` (
    `mno` int NOT NULL AUTO_INCREMENT COMMENT '번호',
    `name` varchar(128) NOT NULL COMMENT '이름',
    `email` varchar(128) NOT NULL COMMENT '이메일',
    `pwd` varchar(256) NOT NULL COMMENT '비밀번호',
    `nickname` varchar(128) NOT NULL COMMENT '닉네임',
    `gender` char(4) NOT NULL COMMENT '성별',
    `birth` date DEFAULT NULL COMMENT '생년월일',
    `phone` varchar(20) NOT NULL COMMENT '연락처',
    `join_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일',
    `active` int NOT NULL DEFAULT '1' COMMENT '활성 상태',
    `role` varchar(20) NOT NULL DEFAULT 'USER',
    PRIMARY KEY (`mno`),
    UNIQUE KEY `email` (`email`),
    UNIQUE KEY `nickname` (`nickname`),
    UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci
COMMENT='회원테이블';


-- 2. 가게 테이블
CREATE TABLE `place` (
    `pno` int NOT NULL AUTO_INCREMENT COMMENT '가게번호',
    `name` varchar(100) NOT NULL COMMENT '가게명',
    `category` varchar(50) NOT NULL COMMENT '카테고리',
    `address` varchar(255) NOT NULL COMMENT '주소',
    `phone` varchar(20) NOT NULL COMMENT '가게 연락처',
    `description` text COMMENT '가게 설명',
    `reg_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    `place_image` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`pno`)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci
COMMENT='가게테이블';


-- 3. 방문 인증 테이블
CREATE TABLE `visit` (
    `vno` int NOT NULL AUTO_INCREMENT COMMENT '방문인증번호',
    `mno` int NOT NULL COMMENT '회원번호',
    `pno` int NOT NULL COMMENT '가게번호',
    `visit_date` date NOT NULL COMMENT '방문일',
    `receipt` varchar(255) NOT NULL COMMENT '영수증 이미지 경로',
    `status` int NOT NULL DEFAULT '0' COMMENT '인증상태 (0:대기, 1:승인, 2:거절)',
    `reg_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '인증 신청일',
    PRIMARY KEY (`vno`),
    KEY `mno` (`mno`),
    KEY `pno` (`pno`),
    CONSTRAINT `visit_ibfk_1`
        FOREIGN KEY (`mno`) REFERENCES `member` (`mno`),
    CONSTRAINT `visit_ibfk_2`
        FOREIGN KEY (`pno`) REFERENCES `place` (`pno`)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci
COMMENT='방문 내역';


-- 4. 리뷰 테이블
CREATE TABLE `review` (
    `rno` int NOT NULL AUTO_INCREMENT COMMENT '리뷰번호',
    `vno` int NOT NULL COMMENT '방문인증번호',
    `rating` int NOT NULL COMMENT '별점',
    `content` text NOT NULL COMMENT '리뷰내용',
    `review_image` varchar(255) DEFAULT NULL COMMENT '리뷰사진 경로',
    `reg_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '리뷰작성일',
    PRIMARY KEY (`rno`),
    UNIQUE KEY `vno` (`vno`),
    CONSTRAINT `review_ibfk_1`
        FOREIGN KEY (`vno`) REFERENCES `visit` (`vno`)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci
COMMENT='리뷰 목록';