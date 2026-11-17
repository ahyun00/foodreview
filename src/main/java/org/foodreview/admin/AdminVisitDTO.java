package org.foodreview.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminVisitDTO {
	private Integer vno; // 방문 인증 번호 
	private String nickname; //신청 회원 닉네임 
	private String placeName; // 가게 이름 
	private LocalDate visitDate; // 방문 날짜 
	private String receipt; // 영수증 
	private Integer status; // 0: 대기, 1: 승인, 2: 거절 
	private LocalDateTime regDate; // 신청 날짜 
}
