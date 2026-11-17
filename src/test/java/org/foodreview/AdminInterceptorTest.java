package org.foodreview;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.foodreview.interceptor.AdminInterceptor;
import org.foodreview.member.Member;
import org.foodreview.member.MemberService;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AdminInterceptorTest {
	
	@Test
	void 일반회원_관리자접근차단() throws Exception {
		
		// given
		MemberService memberService = mock(MemberService.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);
		
		AdminInterceptor interceptor = new AdminInterceptor(memberService);
		
		Member loginMember = new Member();
		loginMember.setMno(10);
		
		Member currentMember = new Member();
		currentMember.setMno(10);
		currentMember.setActive(1);
		currentMember.setRole("USER");
		
		// 기존 세션이 있는 상황을 가정 
		when(request.getSession(false)).thenReturn(session);
		
		// 세션에서 로그인 회원을 가져오는 상황을 가정
		when(session.getAttribute("member")).thenReturn(loginMember);
	
		// DB에서 일반 회원 정보를 조회한 상황을 가정 
		when(memberService.findByMno(10)).thenReturn(currentMember);
		
		// when: 관리자 인터셉터 실행
		boolean result = interceptor.preHandle(request, response, new Object());
		
		// then: 일반 회원의 관리자 접근이 차단됐는지 검증
		assertFalse(result);
		
		// 메인 페이지로 이동시키는지 검증
		verify(response).sendRedirect("/");
		
		// 일반 회원 접근 차단 시 세션은 종료하지 않는지 검증
		verify(session, never()).invalidate();
	}
	
	
		@Test
		void 관리자_접근허용() throws Exception {
			
			// given
		    MemberService memberService = mock(MemberService.class);
		    HttpServletRequest request = mock(HttpServletRequest.class);
		    HttpServletResponse response = mock(HttpServletResponse.class);
		    HttpSession session = mock(HttpSession.class);

		    AdminInterceptor interceptor = new AdminInterceptor(memberService);

		    Member loginMember = new Member();
		    loginMember.setMno(10);

		    Member currentMember = new Member();
		    currentMember.setMno(10);
		    currentMember.setActive(1);
		    currentMember.setRole("ADMIN");

		    when(request.getSession(false)).thenReturn(session);
		    when(session.getAttribute("member")).thenReturn(loginMember);
		    when(memberService.findByMno(10)).thenReturn(currentMember);
		    
		    // when
		    boolean result = interceptor.preHandle(request, response, new Object());
		    
		    // then

		    // 관리자는 Controller 실행이 허용돼야 함
		    assertTrue(result);

		    // 로그인 페이지나 메인 페이지로 리다이렉트하지 않아야 함
		    verify(response, never()).sendRedirect(anyString());
		    
		    // 정상 회원이므로 세션도 종료하지 않아야 함
		    verify(session, never()).invalidate();
		}
		
		@Test
		void 비활성회원_관리자접근차단() throws Exception {
			
			// given
			MemberService memberService = mock(MemberService.class);
		    HttpServletRequest request = mock(HttpServletRequest.class);
		    HttpServletResponse response = mock(HttpServletResponse.class);
		    HttpSession session = mock(HttpSession.class);

		    AdminInterceptor interceptor = new AdminInterceptor(memberService);

		    Member loginMember = new Member();
		    loginMember.setMno(10);

		    Member currentMember = new Member();
		    currentMember.setMno(10);
		    currentMember.setActive(0);     
		    currentMember.setRole("ADMIN");
		    
		    // 로그인 세션이 존재한다.
		    when(request.getSession(false)).thenReturn(session);
		    
		    // 세션에 회원번호 10인 로그인 회원이 있다.
		    when(session.getAttribute("member")).thenReturn(loginMember);
		    
		    // DB에서 비활성 회원 정보가 조회된다.
		    when(memberService.findByMno(10)).thenReturn(currentMember);
		    
		    // when: 실제 AdminInterceptor 실행
		    boolean result = interceptor.preHandle(request, response, new Object());
		    
		    // then: Controller 실행을 차단하는지 검증
		    assertFalse(result);
		    
		    verify(session).invalidate();
		    verify(response).sendRedirect("/login");
		    

		    

			
		}

}












