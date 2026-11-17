package org.foodreview.review;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewMapper reviewMapper;
	
	// 리뷰 등록
	public int insertReview(Review review) {
		
		// 별점, 내용 검증
		validateReview(review);
		
		return reviewMapper.insertReview(review);
	}
	
	// 리뷰 목록 조회
	public List<Review> findAll(){
		return reviewMapper.findAll();
	}
	
	// 리뷰 상세 조회
	public Review findByRno(Integer rno) {
		return reviewMapper.findByRno(rno);
	}
	
	// 리뷰 수정
	public int updateReview(Review review) {
		
		validateReview(review);
		
		return reviewMapper.updateReview(review);
	}
	
	// 리뷰 삭제
	public int deleteReview(Integer rno) {
		return reviewMapper.deleteReview(rno);
	}
	
	
	// 리뷰 입력값 검증 (등록, 수정 공통 사용)	
	private void validateReview(Review review) {
		
		// 별점이 없거나 1~5점 범위를 벗어나면 예외 발생
	    if (review.getRating() == null
	            || review.getRating() < 1
	            || review.getRating() > 5) {

	        throw new IllegalArgumentException("별점은 1~5점만 가능합니다.");
	    }

	    // 리뷰 내용이 없거나 공백만 입력된 경우 예외 발생
	    if (review.getContent() == null
	            || review.getContent().isBlank()) {

	        throw new IllegalArgumentException("리뷰 내용을 입력해 주세요.");
	    }
		
	}
	
	// 해당 방문 인증에 이미 작성된 리뷰가 있는지 확인
	public boolean hasReview(Integer vno) {
		return reviewMapper.countByVno(vno) > 0;
	}
	
	// 리뷰 페이징 조회
	public List<Review> findByPage (int page){
		
		int size = 10;
		
		int offset = (page - 1) * size;
		
		// 계산한 size와 offset을 Mapper에 전달
		return reviewMapper.findByPage(size, offset);
	}
	
	// 전체 리뷰 개수 조회
	public int countAll() {
		return reviewMapper.countAll();
	}
	
	// 메인 페이지에 표시할 최신 리뷰
	public List<Review> getHomeReviews() {
		return reviewMapper.findRecentApprovedReviews();
	}
	
}





















