package org.foodreview;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.foodreview.member.MemberService;
import org.foodreview.visit.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdminVisitApiWebConfigTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@org.springframework.test.context.bean.override.mockito.MockitoBean
	private VisitService visitService;
	
	@org.springframework.test.context.bean.override.mockito.MockitoBean
	private MemberService memberService;
	
	@Test
	void unauthenticatedApiRequest_returns401() throws Exception {
		
		mockMvc.perform(
				patch("/api/admin/visits/10/approve")
				).andExpect(status().isUnauthorized());
	}

}
