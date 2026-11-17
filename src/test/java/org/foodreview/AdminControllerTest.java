package org.foodreview;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.foodreview.admin.AdminController;
import org.foodreview.file.ReceiptFileService;
import org.foodreview.member.MemberService;
import org.foodreview.visit.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

public class AdminControllerTest {

	@Test
	void 이미_처리된_방문은_중복승인차단() {
		
		// given
		MemberService memberService = mock(MemberService.class);
		VisitService visitService = mock(VisitService.class);
		ReceiptFileService receiptFileService = mock(ReceiptFileService.class);
		
		AdminController controller = new AdminController(memberService, visitService, receiptFileService);

        Integer vno = 10;

        // 이미 처리된 방문이므로 변경된 행이 0개라고 가정
        when(visitService.approveVisit(vno)).thenReturn(0);
        
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        
        // when
       String result = controller.approveVisit(vno, redirectAttributes);
       
       // then
       assertEquals("redirect:/admin/visits", result);
       
       assertEquals("이미 처리되었거나 존재하지 않는 방문 인증입니다.",
    		   redirectAttributes.getFlashAttributes().get("message"));
       
       verify(visitService, times(1)).approveVisit(vno);
	}
	
	
	@Test
	void 방문인증_승인성공() {
		
		//given
		MemberService memberService = mock(MemberService.class);
		VisitService visitService = mock(VisitService.class);
		ReceiptFileService receiptFileService = mock(ReceiptFileService.class);
		
		AdminController controller = new AdminController(memberService, visitService, receiptFileService);
		
		Integer vno = 10;
		
		// 승인 성공: DB에서 1개 행이 변경된 상황
		when(visitService.approveVisit(vno)).thenReturn(1);
		
		RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
		
		// when
		String result = controller.approveVisit(vno, redirectAttributes);
		
		// then
		assertEquals("redirect:/admin/visits", result);
		
		assertEquals("방문 인증이 승인되었습니다.", 
				redirectAttributes.getFlashAttributes().get("message"));
		
		verify(visitService, times(1)).approveVisit(vno);
		
	}
	
	@Test
	void 방문인증_거절성공() {
		
		// given
		MemberService memberService = mock(MemberService.class);
		VisitService visitService = mock(VisitService.class);
	    ReceiptFileService receiptFileService = mock(ReceiptFileService.class);
		
		AdminController  controller = new AdminController(memberService, visitService, receiptFileService);
	
		Integer vno =10;
		
		when(visitService.rejectVisit(vno)).thenReturn(1);
		
		RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
		
		// when 
		String result = controller.rejectVisit(vno, redirectAttributes);
		
		// then
		assertEquals("redirect:/admin/visits", result);
		
		assertEquals("방문 인증이 거절되었습니다.",
				redirectAttributes.getFlashAttributes().get("message"));
		
		verify(visitService, times(1)).rejectVisit(vno);
	}
	
	
	@Test
	void 방문인증_거절실패시_안내메시지확인() {

	    // given
	    MemberService memberService = mock(MemberService.class);
	    VisitService visitService = mock(VisitService.class);
	    ReceiptFileService receiptFileService = mock(ReceiptFileService.class);

	    AdminController controller =
	            new AdminController(memberService, visitService, receiptFileService);

	    Integer vno = 10;
	    
	    when(visitService.rejectVisit(vno)).thenReturn(0);
	    
	    RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
	    
	    // when
	    String result = controller.rejectVisit(vno, redirectAttributes);
	    
	    // then
	    assertEquals("redirect:/admin/visits", result);
	    
	    assertEquals("이미 처리되었거나 존재하지 않는 방문 인증입니다.",
	    		redirectAttributes.getFlashAttributes().get("message"));
	    
	    verify(visitService, times(1)).rejectVisit(vno);
	 
	    
	}

}
























