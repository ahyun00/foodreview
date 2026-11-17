package org.foodreview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.foodreview.file.ReceiptFileService;
import org.foodreview.member.Member;
import org.foodreview.visit.Visit;
import org.foodreview.visit.VisitController;
import org.foodreview.visit.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

public class VisitControllerTest {
	
	@Test
	void DB등록실패시_영수증파일삭제() {

        // given
		VisitService visitService = mock(VisitService.class);
		ReceiptFileService receiptFileService = mock(ReceiptFileService.class);
		HttpSession session = mock(HttpSession.class);
		MultipartFile receiptFile = mock(MultipartFile.class);
		
		VisitController controller = new VisitController(visitService, receiptFileService);
		
		// 로그인 회원 설정
		Member member = new Member();
		member.setMno(10);
		
		when(session.getAttribute("member")).thenReturn(member);
		
		// 방문 인증 정보 설정
		Visit visit = new Visit();
		visit.setPno(1);
		visit.setVisitDate(LocalDate.now());
		
		// 영수증 파일 저장은 성공했다고 가정
		when(receiptFileService.saveReceipt(receiptFile)).thenReturn("test.jpg");
		
		// DB 등록 과정에서는 오류가 발생한다고 가정
		doThrow(new RuntimeException("DB 등록 실패"))
						.when(visitService).insertVisit(visit);
		
		Model model = new ExtendedModelMap();
		
		// when: 실제 Controller의 방문 인증 등록 메서드 실행
		String result = controller.register(visit, receiptFile, session, model);
		
		// then: 오류 화면으로 돌아가는지 확인
		assertEquals("visit/register", result);
		
		// 저장된 영수증 파일 삭제 메서드가 호출됐는지 확인
		verify(receiptFileService).deleteReceipt("test.jpg");
		
		// 사용자에게 오류 메시지를 전달하는지 확인
		assertEquals("방문 인증 등록 중 오류가 발생했습니다.",model.getAttribute("error"));
	}
	
	
	@Test
	void DB등록과_파일삭제가_모두실패해도_오류화면반환() {

	    // given
		VisitService visitService = mock(VisitService.class);
		ReceiptFileService receiptFileService = mock(ReceiptFileService.class);
		HttpSession session = mock(HttpSession.class);
		MultipartFile receiptFile = mock(MultipartFile.class);
		VisitController controller = new VisitController(visitService, receiptFileService);
		
		Member member = new Member();
		member.setMno(10);
		
		when(session.getAttribute("member")).thenReturn(member);
		
		Visit visit = new Visit();
		visit.setPno(1);
		visit.setVisitDate(LocalDate.now());
		
		when(receiptFileService.saveReceipt(receiptFile)).thenReturn("test.jpg");
		
		// DB 등록 실패
		doThrow(new RuntimeException("DB 등록 실패"))
						.when(visitService).insertVisit(visit);
		
		// 파일 삭제도 실패
		doThrow(new RuntimeException("파일 삭제 실패"))
						.when(receiptFileService).deleteReceipt("test.jpg");
		
		Model model = new ExtendedModelMap();
		
		// when
		String result = controller.register(visit, receiptFile, session, model);
		
		 // then
	    assertEquals("visit/register", result);

		verify(receiptFileService).deleteReceipt("test.jpg");
		
		assertEquals("방문 인증 등록 중 오류가 발생했습니다.", model.getAttribute("error"));
	}	
	
	
	@Test
	void 파일저장실패시_DB등록하지않음() {

	    // given
	    VisitService visitService = mock(VisitService.class);
	    ReceiptFileService receiptFileService =
	            mock(ReceiptFileService.class);

	    HttpSession session = mock(HttpSession.class);
	    MultipartFile receiptFile = mock(MultipartFile.class);

	    VisitController controller =
	            new VisitController(visitService, receiptFileService);

	    Member member = new Member();
	    member.setMno(10);
	    when(session.getAttribute("member")).thenReturn(member);

	    Visit visit = new Visit();
	    visit.setPno(1);
	    visit.setVisitDate(LocalDate.now());

	    // 영수증 저장 과정에서 오류 발생
	    when(receiptFileService.saveReceipt(receiptFile))
	    				.thenThrow(new IllegalStateException("파일 저장 실패"));
	    
	    Model model = new ExtendedModelMap();
	    
	    // when
	    String result = controller.register(visit, receiptFile, session, model);
	    
	    // then
	    assertEquals("visit/register", result);
	    
	    // 파일 저장에 실패했으므로 DB 등록은 실행하면 안 됨
	    verify(visitService, never()).insertVisit(any(Visit.class));
	    
	    // 저장된 파일명이 없으므로 삭제도 시도하면 안 됨
	    verify(receiptFileService, never()).deleteReceipt(anyString());
	    
	    assertEquals("방문 인증 등록 중 오류가 발생했습니다.", model.getAttribute("error"));
	}
	
	
	@Test
	void 미래날짜면_파일저장하지않음() {

	    VisitService visitService = mock(VisitService.class);
	    ReceiptFileService receiptFileService = mock(ReceiptFileService.class);
	    HttpSession session = mock(HttpSession.class);
	    MultipartFile receiptFile = mock(MultipartFile.class);

	    VisitController controller =
	            new VisitController(visitService, receiptFileService);

	    Member member = new Member();
	    member.setMno(10);
	    when(session.getAttribute("member")).thenReturn(member);

	    Visit visit = new Visit();
	    visit.setPno(1);
	    visit.setVisitDate(LocalDate.now().plusDays(1));
	    
	    // 날짜 검증에서 예외 발생하도록 설정
	    doThrow(new IllegalArgumentException("미래 날짜는 선택할 수 없습니다."))
	    				.when(visitService).validateVisitDate(visit.getVisitDate());
	    
	    Model model = new ExtendedModelMap();
	    
	    String result = controller.register(visit, receiptFile, session, model);
	    
	    assertEquals("visit/register", result);
	    
	    verify(receiptFileService, never()).saveReceipt(any(MultipartFile.class));
	    verify(visitService, never()).insertVisit(any(Visit.class));
	    
	    assertEquals("미래 날짜는 선택할 수 없습니다.", model.getAttribute("error"));
	}
}















