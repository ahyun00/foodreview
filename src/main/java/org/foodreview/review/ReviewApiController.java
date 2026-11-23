package org.foodreview.review;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewApiController {

	private final ReviewService reviewService;
	
	@GetMapping("/api/reviews")
	public ResponseEntity<Map<String, Object>> reviewList(
			@RequestParam(name = "page",defaultValue = "1") int page,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "rating", required = false) Integer rating) {
		
		if (page < 1) {
			page =1;
		}
		
		if (rating != null && (rating < 1 || rating > 5)) {
			return ResponseEntity.badRequest()
							.body(Map.of("message", "별점은 1~5점만 가능합니다."));
		}
		
		List<Review> reviews = reviewService.searchReviews(keyword, rating, page);
		
		int totalCount = reviewService.countSearchReviews(keyword, rating);
		
		int totalPages = (int) Math.ceil(totalCount / 10.0);
		
		return ResponseEntity.ok(Map.of(
				"reviews", reviews,
				"page", page,
				"totalCount", totalCount,
				"totalPages", totalPages
		));
	}
	
}









