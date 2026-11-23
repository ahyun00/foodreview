package org.foodreview.review;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewMapper {
	
	// 리뷰 등록
	int insertReview(@Param("review")  Review review);
	
	// 리뷰 목록 조회
	List<Review> findAll();
	
	// 리뷰 상세 조회
	Review findByRno(@Param("rno") Integer rno);
	
	// 리뷰 수정
	int updateReview(@Param("review") Review review);
	
	// 리뷰 삭제
	int deleteReview(@Param("rno") Integer rno);
	
	// 특정 방문 인증에 작성된 리뷰가 있는지 확인
	int countByVno(@Param("vno") Integer vno);
	
	// 리뷰 목록 페이징 조회
	List<Review> findByPage(@Param("size") int size, @Param("offset") int offset);
	
	// 전체 리뷰 개수 조회
	int countAll();
	
	// 검색 조건에 맞는 리뷰 페이징 조회
	List<Review> searchReviews(
			@Param("keyword") String keyword,
			@Param("rating") Integer rating,
			@Param("size") int size,
			@Param("offset") int offset
	);
	
	// 검색 조건에 맞는 전체 리뷰 개수 조회
	int countSearchReviews(
			@Param("keyword") String keyword,
			@Param("rating") Integer rating
	);
	
	
	List<Review> findRecentApprovedReviews();
}







