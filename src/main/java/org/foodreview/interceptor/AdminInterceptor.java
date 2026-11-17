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
public class AdminInterceptor implements HandlerInterceptor {
	
	// DB에서 최신 회원 정보를 조회할 때 사용
	private final MemberService memberService;
	
	 // Controller가 실행되기 전에 호출되는 메서드
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler ) throws Exception {
		
		// 기존 세션 가져오기
		// false = 세션이 없어도 새로 만들지 않음
		HttpSession session = request.getSession(false);
		
		// 기존 세션이 없으면
		if (session == null) {
			response.sendRedirect("/login");
			return false; //Controller 실행 중단
		}
		
		// 세션에 저장된 로그인 회원 정보 가져오기
		Member loginMember = (Member)session.getAttribute("member");
		
		// 로그인 회원 정보가 없다면 로그인 화면으로 이동
		if (loginMember == null) {
			response.sendRedirect("/login");
			return false;
		}
		
		// DB에서 최신 회원 정보 조회
		Member member = memberService.findByMno(loginMember.getMno());
		
		// 탈퇴하거나 비활성화된 회원이면 세션 종료
		if (member == null || member.getActive() == 0) {
			session.invalidate();
			response.sendRedirect("/login");
			return false;
		}
		
		 // ADMIN 권한이 아니면 메인 페이지로 이동
		if (!"ADMIN".equals(member.getRole())) {
			response.sendRedirect("/");
			return false;
		}
		
		return true;
		
	}

}












