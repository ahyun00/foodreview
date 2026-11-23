package org.foodreview.interceptor;

import org.foodreview.member.Member;
import org.foodreview.member.MemberService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
//스프링 MVC의 HandlerInterceptor 인터페이스 구현
public class LoginInterceptor implements HandlerInterceptor  {
		
		private final MemberService memberService;
		
		@Override
		public boolean preHandle(
				HttpServletRequest request, 
				HttpServletResponse response, 
				Object handler) throws Exception {
			
			if("/api/reviews".equals(request.getRequestURI())
					&& "GET".equalsIgnoreCase(request.getMethod())) {
				return true;
			}
			
			boolean apiRequest = request.getRequestURI().startsWith("/api/");
			
			// 기존 세션을 가져옴
			// false = 세션이 없더라도 새로 만들지 않음
			HttpSession session = request.getSession(false);
			
			if (session == null) {
				if(apiRequest) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				} else {
					response.sendRedirect("/login");
				}
				return false; // Controller 실행 중단
			}
			
			Member loginMember = (Member) session.getAttribute("member");
			
			if (loginMember == null) {
				if (apiRequest) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				} else {
					response.sendRedirect("/login");
				}
				return false;
			}
			
			Member currentMember = memberService.findByMno(loginMember.getMno());
			
			if (currentMember == null || currentMember.getActive() == 0) {
				session.invalidate();
				if (apiRequest) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				} else {
					response.sendRedirect("/login");
				}
				return false;
			}
			
			return true; // 모든 검사를 통과했으므로 Controller 실행 허용
		}
	

}












