package org.foodreview;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.foodreview.file.ReviewImageFileService;
import org.foodreview.member.Member;
import org.foodreview.review.Review;
import org.foodreview.review.ReviewApiController;
import org.foodreview.review.ReviewService;
import org.foodreview.visit.Visit;
import org.foodreview.visit.VisitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class ReviewApiControllerTest {
	
	private MockMvc mockMvc;
	private ReviewService reviewService;
	private VisitService visitService;
	private ReviewImageFileService reviewImageFileService;
	
	@BeforeEach
	void setUp() {
		reviewService = mock(ReviewService.class);
		visitService = mock(VisitService.class);
		reviewImageFileService = mock(ReviewImageFileService.class);
		
		mockMvc = MockMvcBuilders
						.standaloneSetup(
								new ReviewApiController(
										reviewService, visitService,reviewImageFileService))
						.build();
	}
	
	@Test
	void reviewList_returnsPageInfo() throws Exception {
		
		// 검색 조건 없이 1페이지를 조회하는 상황
		when(reviewService.searchReviews(null, null, 1))
						.thenReturn(Collections.emptyList());
		
		// 전체 리뷰가 16개라고 가정
		when(reviewService.countSearchReviews(null, null)).thenReturn(16);
		
		// API 요청 및 응답 검증
		mockMvc.perform(get("/api/reviews").param("page", "1"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.page").value(1))
						.andExpect(jsonPath("$.totalCount").value(16))
						.andExpect(jsonPath("$.totalPages").value(2))
						.andExpect(jsonPath("$.reviews").isArray());
	}
	
	@Test
	void reviewLisst_pageZeroChangesToOne() throws Exception {
		
		mockMvc.perform(get("/api/reviews").param("page", "0"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.page").value(1));
		
		verify(reviewService).searchReviews(null, null, 1);
	}
	
	@Test
	void reviewList_pageTwo() throws Exception {
		
		when(reviewService.countSearchReviews(null, null)).thenReturn(16);
		
		mockMvc.perform(get("/api/reviews").param("page", "2"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.page").value(2))
						.andExpect(jsonPath("$.totalPages").value(2));
		
		verify(reviewService).searchReviews(null, null, 2);
	}
	
	@Test
	void reviewList_keywordSearch() throws Exception {
		
		// 검색어가 '카페'인 리뷰가 4개라고 가정
		when(reviewService.countSearchReviews("카페", null))
						.thenReturn(4);
		
		// 검색 요청
		mockMvc.perform(get("/api/reviews").param("keyword", "카페"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.totalCount").value(4))
						.andExpect(jsonPath("$.totalPages").value(1));
		
		// 검색어와 페이지 번호가 Service에 전달됐는지 검증
		verify(reviewService).searchReviews("카페", null, 1);
	}
	
	@Test
	void reviewList_ratingFilter() throws Exception {
		
		// 별점 5점인 리뷰가 7개라고 가정
		when(reviewService.countSearchReviews(null, 5)).thenReturn(7);
		
		// 별점 5점 필터 요청
		mockMvc.perform(get("/api/reviews").param("rating", "5"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.totalCount").value(7))
						.andExpect(jsonPath("$.totalPages").value(1));
		
		// Service에 별점 5와 페이지 1이 전달됐는지 확인
		verify(reviewService).searchReviews(null, 5, 1);
	}
	
	@Test
	void reviewList_keywordAndRatingFilter() throws Exception {

	    // 검색어 '카페' + 별점 5점에 해당하는 리뷰가 2개라고 가정
		when(reviewService.countSearchReviews("카페", 5)).thenReturn(2);
		
		// 검색어와 별점을 동시에 전달
		mockMvc.perform(get("/api/reviews")
				        .param("keyword", "카페").param("rating", "5"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.totalCount").value(2))
						.andExpect(jsonPath("$.totalPages").value(1));
		
		// 두 검색 조건이 Service에 정확히 전달됐는지 검증
		verify(reviewService).searchReviews("카페", 5, 1);
	}
	
	@Test
	void reviewList_invalidRatingReturns400() throws Exception {
		
		mockMvc.perform(get("/api/reviews").param("rating", "6"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.message")
								.value("별점은 1~5점만 가능합니다."));
		
		verifyNoInteractions(reviewService);
	}
	
	@Test
	void createReview_successReturns201() throws Exception {
		
		// 로그인한 회원
		Member member = new Member();
		member.setMno(10);
		
		// 본인의 승인된 방문 인증이라고 가정
		when(visitService.findApprovedVisit(118, 10)).thenReturn(new Visit());
		
		// DB에 리뷰 1개 등록 성공이라고 가정
		when(reviewService.insertReview(any(Review.class))).thenReturn(1);
		
		// 리뷰 등록 요청
		mockMvc.perform(multipart("/api/reviews")
										.param("vno", "118")
										.param("rating", "5")
										.param("content", "정말 맛있었어요.")
										.sessionAttr("member", member))
						.andExpect(status().isCreated())
						.andExpect(jsonPath("$.message")
										.value("리뷰가 등록되었습니다."));
		
		// 실제로 등록 메서드를 호출했는지 확인
		verify(reviewService).insertReview(any(Review.class));
	}
	
	@Test
	void createReview_missingVnoReturns400() throws Exception {
		
		// 로그인한 회원
	    Member member = new Member();
	    member.setMno(10);

	    // 방문 인증 번호(vno)를 제외하고 리뷰 등록 요청
	    mockMvc.perform(multipart("/api/reviews")
	    								.param("rating", "5")
	    								.param("content", "정말 맛있었어요.")
	    								.sessionAttr("member", member))
	    				.andExpect(status().isBadRequest())
	    				.andExpect(jsonPath("$.message")
	    								.value("방문 인증 번호를 입력해 주세요."));
	    
    	// 방문 번호가 없으므로 서비스는 호출되지 않아야 함
	   verifyNoInteractions(reviewService, visitService, reviewImageFileService);
	}
	
	
	@Test
	void createReview_otherMembersVisitReturns403() throws Exception {
		
		// given: 로그인한 회원 번호는 10
	    Member member = new Member();
	    member.setMno(10);

	    // 118번 방문은 로그인한 회원의 승인된 방문이 아니라고 가정
	    when(visitService.findApprovedVisit(118, 10))
	    				.thenReturn(null);
	    
	    // when & then: 리뷰 등록 요청 후 403 확인
	    mockMvc.perform(multipart("/api/reviews")
	    								.param("vno", "118")
	    								.param("rating", "5")
	    								.param("content", "정말 맛있었어요.")
	    								.sessionAttr("member", member))
	    				.andExpect(status().isForbidden())
	    				.andExpect(jsonPath("$.message")
	    						.value("본인의 승인된 방문 인증이 아닙니다."));
	    
	     // 리뷰가 실제로 등록되지 않았는지 확인
	    verifyNoInteractions(reviewService, reviewImageFileService);
	}
	
	@Test
	void createReview_duplicateReturns409() throws Exception {
		
		// 로그인한 회원
	    Member member = new Member();
	    member.setMno(10);

	    // 본인의 승인된 방문이라고 가정
	    when(visitService.findApprovedVisit(118, 10))
	    				.thenReturn(new Visit());
	    
	    // 이미 리뷰가 등록되어 있다고 가정
	    when(reviewService.hasReview(118))
	    				.thenReturn(true);
	    
	    // 리뷰 등록 요청
	    mockMvc.perform(multipart("/api/reviews")
	    								.param("vno", "118")
	    								.param("rating", "5")
	    								.param("content", "정말 맛있었어요.")
	    								.sessionAttr("member", member))
	    				.andExpect(status().isConflict())
	    				.andExpect(jsonPath("$.message")
	    									.value("이미 리뷰가 등록된 방문입니다."));
	    
	 	// 중복 리뷰이므로 등록 메서드는 실행되지 않아야 함
	    verify(reviewService).hasReview(118);
	    verify(reviewService, never()).insertReview(any(Review.class));
	}
}











