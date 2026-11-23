package org.foodreview;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.foodreview.interceptor.LoginInterceptor;
import org.foodreview.member.Member;
import org.foodreview.member.MemberService;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginInterceptorTest {
	
	@Test
	void 비활성회원_접근차단() throws Exception {
		
		// given
		MemberService memberService = mock(MemberService.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);
		
		LoginInterceptor interceptor = new LoginInterceptor(memberService);
		
		Member loginMember = new Member();
		loginMember.setMno(10);
		
		Member currentMember = new Member();
		currentMember.setMno(10);
		currentMember.setActive(0);
		
		when(request.getRequestURI()).thenReturn("/mypage");
		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("member")).thenReturn(loginMember);
		when(memberService.findByMno(10)).thenReturn(currentMember);
		
		// when
		boolean result = interceptor.preHandle(request, response, new Object());
		
		// then
		assertFalse(result);
		verify(session).invalidate();
		verify(response).sendRedirect("/login");
		
	}
	
	
	@Test
	void 비로그인_API요청_401반환() throws Exception {

	    // given: 로그인 세션이 없는 상태
		MemberService memberService = mock(MemberService.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		LoginInterceptor interceptor = new LoginInterceptor(memberService);
		
		when(request.getRequestURI()).thenReturn("/api/reviews");
		when(request.getMethod()).thenReturn("POST");
		when(request.getSession(false)).thenReturn(null);
		
		// when: 인터셉터 실행
		boolean result = interceptor.preHandle(request, response, new Object());
		
		// then: 요청 차단 + HTTP 401 반환
		assertFalse(result);
		verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
		verifyNoInteractions(memberService);
	}
}






















