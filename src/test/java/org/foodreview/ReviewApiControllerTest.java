package org.foodreview;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
		
		// 가짜 Service가 반환할 값을 설정
		when(reviewService.findByPage(1))
						.thenReturn(Collections.emptyList());
		
		when(reviewService.countAll()).thenReturn(16);
		
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
		
		verify(reviewService).findByPage(1);
	}
	
	@Test
	void reviewList_pageTwo() throws Exception {
		
		when(reviewService.countAll()).thenReturn(16);
		
		mockMvc.perform(get("/api/reviews").param("page", "2"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.page").value(2))
						.andExpect(jsonPath("$.totalPages").value(2));
		
		verify(reviewService).findByPage(2);
	}

}











