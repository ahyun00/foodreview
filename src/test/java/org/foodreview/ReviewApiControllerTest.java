package org.foodreview;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.foodreview.review.ReviewApiController;
import org.foodreview.review.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class ReviewApiControllerTest {
	
	private MockMvc mockMvc;
	private ReviewService reviewService;
	
	@BeforeEach
	void setUp() {
		reviewService = mock(ReviewService.class);
		
		mockMvc = MockMvcBuilders
						.standaloneSetup(new ReviewApiController(reviewService))
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
}











