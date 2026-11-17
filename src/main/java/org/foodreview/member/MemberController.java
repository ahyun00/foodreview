package org.foodreview.member;

import java.util.List;

import org.foodreview.visit.Visit;
import org.foodreview.visit.VisitService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {
	
	private final MemberService memberService;
	private final VisitService visitService;
	
	
	// 회원가입 화면
	@GetMapping("/register")
	public String registerForm() {
		return "/member/register";
	}
	
	// 회원가입 처리
	@PostMapping("/register")
	public String register(Member member, Model model) {
		
		// 필수 항목 검사
		if(member.getName() == null || member.getName().isBlank()
				|| member.getEmail() == null || member.getEmail().isBlank()
				|| member.getPwd() == null || member.getPwd().isBlank()
				|| member.getNickname() == null || member.getNickname().isBlank()
				|| member.getGender() == null || member.getGender().isBlank()
				|| member.getBirth() == null
				|| member.getPhone() == null || member.getPhone().isBlank()) {
			
			model.addAttribute("error", "모든 항목을 입력해 주세요.");
			model.addAttribute("member", member);
			return "member/register";
		}
		
		// 전화번호 형식 검사
		if (!member.getPhone().matches("^010[0-9]{8}$")) {
			model.addAttribute("error", "전화번호는 010으로 시작하는 11자리 숫자로 입력해 주세요.");
			model.addAttribute("member", member);
			return "member/register";
		}
		
		try {
			int result = memberService.register(member);
			
			if (result == 1) {
				return "redirect:/";
			}
			
			model.addAttribute("error", "회원가입에 실패했습니다. 다시 시도해 주세요.");
			model.addAttribute("member", member);
			return "member/register";
			
		} catch (DataIntegrityViolationException e) {
			
			String message = "이미 사용 중인 정보가 있습니다.";

			// DB의 UNIQUE 제약조건 이름으로 중복 항목 구분
			String errorMessage = e.getMostSpecificCause().getMessage();
			
			if (errorMessage != null) {
				
				if (errorMessage.contains("member.email")) {
					message = "이미 사용 중인 이메일입니다.";
					
				} else if (errorMessage.contains("member.nickname")) {
					message = "이미 사용 중인 닉네임입니다.";
					
				} else if (errorMessage.contains("member.phone")) {
					message = "이미 사용 중인 전화번호입니다.";
				}				
			}
			
			model.addAttribute("error", message);
			model.addAttribute("member", member);
			return "member/register";
		}
	}
	
	
	// 로그인 화면
	@GetMapping("/login")
	public String login() {
		return "member/login";
	}
	
	// 로그인 처리
	@PostMapping("/login")
	public String login(Member member, HttpSession session, Model model) {
		
		Member loginMember = memberService.login(member);
		
		if (loginMember == null) {
			model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
			return "member/login";
		}
		
		// 비활성 회원 로그인 차단
		if (loginMember.getActive() == null || loginMember.getActive() !=1) {
			model.addAttribute("error", "비활성화된 계정입니다.");
			return "member/login";
		}
		
		session.setAttribute("member", loginMember);
		return "redirect:/";
	}
	
	
	// 로그아웃
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
		
	}
	
	
	// 마이페이지
	@GetMapping("/mypage")
	public String mypage(HttpSession session, Model model) {
		
		// LoginInterceptor에서 로그인 및 활성 회원 검사를 완료했으므로
	    // 세션에서 로그인한 회원 정보를 가져옴
		Member loginMember = (Member) session.getAttribute("member");
		
		// DB에서 최신 회원정보 조회
		Member member = memberService.findByMno(loginMember.getMno());
		
		// 회원정보를 HTML에 전달
		model.addAttribute("member", member);
		
		// 로그인한 회원의 방문 인증 목록 조회
		List<Visit> myVisits = visitService.findMyVisits(loginMember.getMno());
		
		// 조회한 방문 인증 목록을 HTML에 전달
		model.addAttribute("myVisits", myVisits);
		
		return "member/mypage";
		
	}
	
	
	// 회원정보 수정 화면
	@GetMapping("/mypage/edit")
	public String editForm(HttpSession session, Model model) {
		
		// 현재 로그인한 회원
		Member loginMember = (Member) session.getAttribute("member");
		
		if (loginMember == null) {
			return "redirect:/login";
		}
		
		//  DB에서 현재 회원정보 조회
		Member member = memberService.findByMno(loginMember.getMno());
		
		// 회원정보가 없거나 탈퇴한 회원이면 로그아웃 처리
		if (member == null) {
			session.invalidate();
			return "redirect:/login";
		}
		
		// 수정 화면에 회원정보 전달
		model.addAttribute("member", member);
		
		return "member/edit";
		
	}
	
	// 회원정보 수정 처리
	@PostMapping("/mypage/edit")
	public String edit(Member member, HttpSession session, Model model) {
		
		// 현재 로그인한 회원
		Member loginMember = (Member) session.getAttribute("member");
		
		if (loginMember == null) {
			return "redirect:/login";
		}
		
		// 수정할 회원번호는 세션에서 가져오기
		Integer mno = loginMember.getMno();
		member.setMno(mno);
		
		// DB에서 현재 회원이 정상 회원인지 확인
		Member currentMember = memberService.findByMno(mno);
		
		if (currentMember == null) {
			session.invalidate();
			return "redirect:/login";
		}
		
		// 이름, 닉네임, 전화번호 빈 값 검사
		if (member.getName() == null ||  member.getName().isBlank() || member.getNickname() == null || member.getNickname().isBlank() || member.getPhone() == null || member.getPhone().isBlank()) {
			
			model.addAttribute("error", "모든 항목을 입력해 주세요.");
			model.addAttribute("member", member);
			
			return "member/edit";
		}
		
		// 전화번호 형식 검사 (010으로 시작하는 11자리 숫자)
		if (!member.getPhone().matches("^010[0-9]{8}$")) {
			
			model.addAttribute("error", "전화번호는 010으로 시작하는 11자리 숫자로 입력해 주세요.");
			model.addAttribute("member", member);
			
			return "member/edit";
		}
		
		// 닉네임 중복 확인
		int nicknameCount = memberService.countNicknameForUpdate(member.getNickname(), mno);
		
		if (nicknameCount > 0) {
			model.addAttribute("error", "이미 사용 중인 닉네임입니다.");
			model.addAttribute("member", member);
			return "member/edit";
		}
		
		// 전화번호 중복 확인
		int phoneCount = memberService.countPhoneForUpdate(member.getPhone(), mno);
		
		if (phoneCount > 0) {
			model.addAttribute("error", "이미 사용 중인 전화번호입니다.");
			model.addAttribute("member", member);
			return "member/edit";
		}
		
		// 중복 검사를 통과하면 회원정보 수정
	try {
		memberService.updateMember(member);
	} catch (DataIntegrityViolationException e) {
		
		// DB 제약조건 위반 시 수정 화면으로 돌아가기
		model.addAttribute("error", "이미 사용 중인 정보이거나 저장할 수 없는 값입니다.");
		model.addAttribute("member", member);
		
		return "member/edit";
	}
		
		return "redirect:/mypage";
	}
	
	
	

}


























