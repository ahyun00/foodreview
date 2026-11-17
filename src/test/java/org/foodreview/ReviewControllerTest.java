package org.foodreview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.foodreview.file.ReviewImageFileService;
import org.foodreview.member.Member;
import org.foodreview.review.Review;
import org.foodreview.review.ReviewController;
import org.foodreview.review.ReviewService;
import org.foodreview.visit.Visit;
import org.foodreview.visit.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

public class ReviewControllerTest {
	
	@Test
	void 승인되지않은_방문은_리뷰등록차단() {
		
		ReviewService reviewService = mock(ReviewService.class);
		VisitService visitService = mock(VisitService.class);
		ReviewImageFileService imageService =
		        mock(ReviewImageFileService.class);
		
		ReviewController controller = new ReviewController(reviewService, visitService, imageService);
		
		Member member = new Member();
		member.setMno(1);
		
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("member", member);
		
		Review review = new Review();
		review.setVno(10);
		review.setRating(5);
		review.setContent("맛있어요");
		
		when(visitService.findApprovedVisit(10, 1)).thenReturn(null);
		
		String result = controller.write(review, null, session, new ConcurrentModel());
		
		assertEquals("redirect:/", result);
		
		verify(reviewService, never()).insertReview(any(Review.class));
	}
	
	
	@Test
	void 이미_리뷰가_있으면_중복등록차단() {
		
		ReviewService reviewService = mock(ReviewService.class);
		VisitService visitService = mock(VisitService.class);
		ReviewImageFileService imageService =
		        mock(ReviewImageFileService.class);
		
		ReviewController controller = new ReviewController(reviewService, visitService, imageService);
		
		Member member = new Member();
		member.setMno(1);
		
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("member", member);
		
		Review review = new Review();
		review.setVno(10);
		review.setRating(5);
		review.setContent("맛있어요");
		
		// 승인된 본인 방문이라고 가정
		Visit visit = new Visit();
		when(visitService.findApprovedVisit(10, 1)).thenReturn(visit);
		
		// 이미 리뷰가 존재한다고 가정
		when(reviewService.hasReview(10)).thenReturn(true);
		
		String result = controller.write(review, null, session, new ConcurrentModel());
		
		assertEquals("redirect:/mypage", result);
		
		verify(reviewService, never()).insertReview(any(Review.class));
		
	}
	
	
	@Test
	void 승인된_방문은_리뷰등록성공() {
		
		ReviewService reviewService = mock(ReviewService.class);
		VisitService visitService = mock(VisitService.class);
		ReviewImageFileService imageService =
		        mock(ReviewImageFileService.class);
		
		ReviewController controller = new ReviewController(reviewService, visitService, imageService);
		
		Member member = new Member();
		member.setMno(1);
		
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("member", member);
		
		Review review = new Review();
		review.setVno(10);
	    review.setRating(5);
	    review.setContent("맛있어요");

	    // 본인의 승인된 방문이 존재함
	    when(visitService.findApprovedVisit(10, 1)).thenReturn(new Visit());
	    
	    // 기존에 작성한 리뷰가 없음
	    when(reviewService.hasReview(10)).thenReturn(false);
	    
	    String result = controller.write(review, null, session, new ConcurrentModel());
	    
	    assertEquals("redirect:/", result);       
	    
	    verify(reviewService, times(1)).insertReview(review);
	    
	}
	
	
	@Test
	void 중복리뷰등록시_오류메시지표시() {
		// given
		ReviewService reviewService = mock(ReviewService.class);
		VisitService visitService = mock(VisitService.class);
		ReviewImageFileService imageService =
		        mock(ReviewImageFileService.class);
		
		ReviewController controller = new ReviewController(reviewService, visitService, imageService);
		
		Integer vno = 1;
		
		Member member = new Member();
		member.setMno(6);
		
		Review review = new Review();
		review.setVno(vno);
		review.setRating(5);
		review.setContent("맛있어요");
		
		HttpSession session = mock(HttpSession.class);
		Model model = new ConcurrentModel();
		
		when(session.getAttribute("member")).thenReturn(member);
		
		when(visitService.findApprovedVisit(vno, 6)).thenReturn(new Visit());
		
		when(reviewService.hasReview(vno)).thenReturn(false);
		
		when(reviewService.insertReview(review)).thenThrow(new DuplicateKeyException("중복 리뷰"));
		
		//when
		String result = controller.write(review, null, session, model);
		
		// then

		// 승인된 방문 조회가 실제로 호출됐는지 확인
		verify(visitService).findApprovedVisit(vno, 6);

		// 리뷰 DB 등록 메서드까지 실행됐는지 확인
		verify(reviewService).insertReview(review);

		// 반환된 화면 확인
		assertEquals("review/write", result);

		// 오류 메시지 확인
		assertEquals(
		        "이미 리뷰가 등록된 방문입니다.",
		        model.getAttribute("error")
		);
		
		
	}


}

















