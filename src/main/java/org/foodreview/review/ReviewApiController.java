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
			@RequestParam(name = "page",defaultValue = "1") int page) {
		
		if (page < 1) {
			page =1;
		}
		
		List<Review> reviews = reviewService.findByPage(page);
		
		int totalCount = reviewService.countAll();
		
		int totalPages = (int) Math.ceil(totalCount / 10.0);
		
		return ResponseEntity.ok(Map.of(
				"reviews", reviews,
				"page", page,
				"totalCount", totalCount,
				"totalPages", totalPages
		));
	}
	
}









