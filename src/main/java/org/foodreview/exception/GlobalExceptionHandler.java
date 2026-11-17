package org.foodreview.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	// 여러 Controller에서 발생한 IllegalArgumentException을 공통 처리 
	@ExceptionHandler(IllegalArgumentException.class)
	public String handleIllegalArgumentException(IllegalArgumentException e) {
		
		// 예외 메시지를 서버 콘솔에 출력
		System.out.println("잘못된 요청: " + e.getMessage());
		
		return "redirect:/";
	}

}
