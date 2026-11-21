package org.foodreview.admin;

import java.util.Map;

import org.foodreview.visit.VisitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AdminVisitApiController {
	
	private final VisitService visitService;
	
	// 관리자 방문 인증 승인 API
	@PatchMapping("/api/admin/visits/{vno}/approve")
	public ResponseEntity<Map<String, String>> approveVisit(
			@PathVariable("vno") Integer vno) {
		
		// 기존 승인 로직 재사용
		int result = visitService.approveVisit(vno);
		
		if (result == 1) {
			return ResponseEntity.ok(
					Map.of("message", "방문 인증이 승인되었습니다.")
			);
		}
		
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(Map.of(
						"message", "이미 처리되었거나 존재하지 않는 방문 인증입니다."
				));
	}
	
	@PatchMapping("api/admin/visits/{vno}/reject")
	public ResponseEntity<Map<String, String>> rejectVisit(
					@PathVariable("vno") Integer vno) {
		
		int result = visitService.rejectVisit(vno);
		
		if (result == 1) {
			return ResponseEntity.ok(
					Map.of("message", "방문 인증이 거절되었습니다.")
			);
		}
		
		return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(Map.of(
								"message", 
								"이미 처리되었거나 존재하지 않는 방문 인증입니다."));
	}
}





