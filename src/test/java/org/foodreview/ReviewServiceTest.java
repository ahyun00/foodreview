package org.foodreview;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.foodreview.review.Review;
import org.foodreview.review.ReviewMapper;
import org.foodreview.review.ReviewService;
import org.junit.jupiter.api.Test;

public class ReviewServiceTest {
	
	@Test
	void 별점이_범위를_벗어나면_등록차단() {
		
		ReviewMapper reviewMapper = mock(ReviewMapper.class);
		ReviewService reviewService = new ReviewService(reviewMapper);
		
		Review review = new Review();
		review.setRating(6);
		review.setContent("맛있어요");
		
		assertThrows(IllegalArgumentException.class, 
				( ) -> reviewService.insertReview(review));
		verify(reviewMapper, never()).insertReview(review);
	}
	
	@Test
	void 리뷰_3페이지_offset계산() {
		
		// given
		ReviewMapper reviewMapper = mock(ReviewMapper.class);
		
		ReviewService reviewService = new ReviewService(reviewMapper);
		
		reviewService.findByPage(3);
		
		// then: size=10, offset=20으로 조회했는지 검증
		verify(reviewMapper).findByPage(10, 20);
		 
		 
	}

}














