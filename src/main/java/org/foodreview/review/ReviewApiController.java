package org.foodreview.review;

import java.util.List;
import java.util.Map;

import org.foodreview.file.ReviewImageFileService;
import org.foodreview.member.Member;
import org.foodreview.visit.VisitService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewApiController {

	private final ReviewService reviewService;
	private final VisitService visitService;
	private final ReviewImageFileService reviewImageFileService;
	
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
		
		List<Review> reviews = 
				reviewService.searchReviews(keyword, rating, page);
		
		int totalCount = reviewService.countSearchReviews(keyword, rating);
		
		int totalPages = (int) Math.ceil(totalCount / 10.0);
		
		return ResponseEntity.ok(Map.of(
				"reviews", reviews,
				"page", page,
				"totalCount", totalCount,
				"totalPages", totalPages
		));
	}
	
	@PostMapping("/api/reviews")
	public ResponseEntity<Map<String, Object>> createReview(
			@ModelAttribute Review review,
			@RequestPart(value = "reviewFile", required = false) MultipartFile reviewFile,
			HttpSession session) {
		
		Member loginMember = (Member) session.getAttribute("member");
		
		if (loginMember == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
							.body(Map.of("message", "로그인이 필요합니다."));
		}
		
		if (review.getVno() == null) {
			return ResponseEntity.badRequest()
							.body(Map.of("message", "방문 인증 번호를 입력해 주세요."));
		}
		
		if (visitService.findApprovedVisit(review.getVno(), loginMember.getMno()) == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
							.body(Map.of("message", "본인의 승인된 방문 인증이 아닙니다."));
		}
		
		if (reviewService.hasReview(review.getVno())) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
							.body(Map.of("message", "이미 리뷰가 등록된 방문입니다."));
		}
		
		String savedName = null;
		
		try {
			savedName = reviewImageFileService.saveImage(reviewFile);
			review.setReviewImage(savedName);
			
			int inserted = reviewService.insertReview(review);
			
			if (inserted != 1) {
				throw new IllegalStateException("리뷰 등록에 실패했습니다.");
			}
			
			return ResponseEntity.status(HttpStatus.CREATED)
							.body(Map.of("message", "리뷰가 등록되었습니다."));
		
		} catch (Exception e) {
			
			if (savedName != null) {
				try {
					reviewImageFileService.deleteImage(savedName);
				} catch (Exception deleteException) {
					System.err.println("리뷰 이미지 정리 실패: " 
									+ deleteException.getMessage());
				}
			}
			
			if (e instanceof DuplicateKeyException) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
								.body(Map.of("message", "이미 리뷰가 등록된 방문입니다."));
			}
			
			if (e instanceof IllegalArgumentException) {
				return ResponseEntity.badRequest()
								.body(Map.of("message", e.getMessage()));
			}
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.body(Map.of("message", "리뷰 등록 중 오류가 발생했습니다."));
		}
	}
	
}









