package org.foodreview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.foodreview.admin.AdminVisitApiController;
import org.foodreview.visit.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AdminVisitApiControllerTest {

	private final VisitService visitService = mock(VisitService.class);
	
	private final AdminVisitApiController controller = 
			new AdminVisitApiController(visitService);
	
	@Test
	void approveVisit_success() {
		
		// 10번 방문 승인 시 DB 수정 결과가 1이라고 가정
		when(visitService.approveVisit(10)).thenReturn(1);
		
		// 승인 API 메서드 실행
		ResponseEntity<Map<String, String>> response =
				controller.approveVisit(10);
		
		// HTTP 상태 코드 200 확인
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		// 응답 메시지 확인
		assertEquals("방문 인증이 승인되었습니다.",
						response.getBody().get("message"));
		
		verify(visitService).approveVisit(10);
	}
	
	@Test
	void approveVisit_conflict() {
		
		when(visitService.approveVisit(10)).thenReturn(0);
		
		ResponseEntity<Map<String, String>> response =
				controller.approveVisit(10);
		
		// HTTP 상태 코드 409 확인
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		
		assertEquals("이미 처리되었거나 존재하지 않는 방문 인증입니다.",
				response.getBody().get("message"));
		
		verify(visitService).approveVisit(10);
	}
	
	@Test
	void rejectVisit_success() {
		when(visitService.rejectVisit(10)).thenReturn(1);
		
		ResponseEntity<Map<String, String>> response = 
						controller.rejectVisit(10);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		assertEquals("방문 인증이 거절되었습니다.",
				response.getBody().get("message"));
		
		verify(visitService).rejectVisit(10);
	}
	
	@Test
	void rejectVisit_conflict() {
		
		when(visitService.rejectVisit(10)).thenReturn(0);
		
		ResponseEntity<Map<String, String>> response = 
				controller.rejectVisit(10);
		
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		
		assertEquals("이미 처리되었거나 존재하지 않는 방문 인증입니다.",
				response.getBody().get("message"));
		
		verify(visitService).rejectVisit(10);
	}

}









