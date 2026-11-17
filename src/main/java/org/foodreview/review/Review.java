package org.foodreview.review;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Review {
	private Integer rno; //리뷰번호 
	private Integer vno; //방문 인증 번호 
	private Integer rating; // 별점 
	private String content; // 리뷰 내용 
	private String reviewImage;
	private LocalDateTime regDate;
	private String nickname;
	private String placeName;

}
