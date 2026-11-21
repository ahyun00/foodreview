package org.foodreview;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.foodreview.admin.AdminVisitApiController;
import org.foodreview.interceptor.AdminInterceptor;
import org.foodreview.member.Member;
import org.foodreview.member.MemberService;
import org.foodreview.visit.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AdminVisitApiSecurityTest {
	
	private final VisitService visitService = mock(VisitService.class);
	private final MemberService memberService = mock(MemberService.class);
	private final AdminInterceptor adminInterceptor =
					new AdminInterceptor(memberService);

	private final MockMvc mockMvc = MockMvcBuilders
					.standaloneSetup(new AdminVisitApiController(visitService))
					.addInterceptors(adminInterceptor)
					.build();
	
	@Test
	void unauthenticatedUser_returns401() throws Exception {
		
		mockMvc.perform(patch("/api/admin/visits/10/approve"))
						.andExpect(status().isUnauthorized());
	}
	
	@Test
	void normalUser_return403() throws Exception {
		
		// 일반 회원 객체 생성
		Member member = new Member();
		member.setMno(10);
	    member.setActive(1);
	    member.setRole("USER");
	    
	    // 로그인한 회원 정보가 들어 있는 가짜 세션 생성
	    MockHttpSession session = new MockHttpSession();
	    session.setAttribute("member", member);
	    
	    // DB에서 최신 회원 정보를 조회했을 때 일반 회원 반환
	    when(memberService.findByMno(10)).thenReturn(member);
	    
	    // 일반 회원으로 관리자 승인 API 요청
		mockMvc.perform(
				patch("/api/admin/visits/10/approve")
								.session(session)
				).andExpect(status().isForbidden());
	}
	
	
	@Test
	void adminUser_returns200() throws Exception {
		
		 // 관리자 회원 객체 생성
	    Member admin = new Member();
	    admin.setMno(1);
	    admin.setActive(1);
	    admin.setRole("ADMIN");

	    // 관리자 로그인 세션 생성
	    MockHttpSession session = new MockHttpSession();
	    session.setAttribute("member", admin);
	    
	     // DB에서 관리자 회원이 조회된다고 가정
	    when(memberService.findByMno(1)).thenReturn(admin);
	    
	     // 방문 인증 승인 성공을 가정 
	    when(visitService.approveVisit(10)).thenReturn(1);
	    
	    // 관리자 권한으로 API 요청 후 200 확인
	    mockMvc.perform(
	    		patch("/api/admin/visits/10/approve")
	    				.session(session)
	    		).andExpect(status().isOk());
	    
	    verify(visitService).approveVisit(10);
	}
	
	
}












