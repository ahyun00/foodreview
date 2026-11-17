package org.foodreview;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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

}






















