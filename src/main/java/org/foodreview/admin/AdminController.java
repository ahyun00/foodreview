package org.foodreview.admin;

import java.util.List;

import org.foodreview.file.ReceiptFileService;
import org.foodreview.member.Member;
import org.foodreview.member.MemberService;
import org.foodreview.visit.VisitService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminController {
	
	private final MemberService memberService;
	private final VisitService visitService;
	private final ReceiptFileService receiptFileService;
	
	// 관리자 메인 화면
	@GetMapping("/admin")
	public String admin() {
		
		return "admin/index";
	}
	
	// 관리자 : 승인 대기 중인 방문 인증 목록
	@GetMapping("/admin/visits")
		public String visitList(Model model) {
			
			// DB에서 승인 대기 중인 방문 인증 목록 조회
			List<AdminVisitDTO> visits = visitService.findPendingVisits();
			
			// HTML로 목록 전달
			model.addAttribute("visits", visits);
			
			return "admin/visits";
		}
	
	// 관리자 : 방문 인증 승인
	@PostMapping("/admin/visits/approve")
	public String approveVisit(@RequestParam("vno") Integer vno, RedirectAttributes redirectAttributes) {

		// 승인 처리
		int result = visitService.approveVisit(vno);
		
		// DB 변경 결과에 따라 메시지 구분
		if (result == 1) {
			redirectAttributes.addFlashAttribute("message", "방문 인증이 승인되었습니다.");
		} else {
			redirectAttributes.addFlashAttribute("message", "이미 처리되었거나 존재하지 않는 방문 인증입니다.");
		}
		
		return "redirect:/admin/visits";
	}
	
	// 관리자 : 방문 인증 거절 
	@PostMapping("/admin/visits/reject")
		public String rejectVisit(@RequestParam("vno") Integer vno, RedirectAttributes redirectAttributes) {
			
			// 거절 처리 후 실제 변경된 행의 개수를 받음
			int result = visitService.rejectVisit(vno);
			
			// 처리 결과에 따라 메시지 구분
			if (result ==1) {
				redirectAttributes.addFlashAttribute("message","방문 인증이 거절되었습니다.");
			} else {
				redirectAttributes.addFlashAttribute( "message", "이미 처리되었거나 존재하지 않는 방문 인증입니다.");
			}
		
			return "redirect:/admin/visits";
	}
	
	
	// 관리자 : 전체 회원 목록 조회
	@GetMapping("/admin/members")
	public String memberList(Model model) {
		
		// 전체 회원 목록 조회
		List<Member> members = memberService.findAllMembers();
		
		// HTML에게 전달
		model.addAttribute("members", members);
		
		return "admin/members";
		
	}
	
	// 관리자: 회원 활성화 / 비활성화
	@PostMapping("/admin/members/active")
	public String updateMemberActive(@RequestParam("mno") Integer mno,@RequestParam("active") Integer active, RedirectAttributes redirectAttributes) {
		
		try {
			 int result = memberService.updateMemberActive(mno, active);
			 
			 if(result == 1) {
				 String message = (active ==1) ?  "회원이 활성화되었습니다." : "회원이 비활성화되었습니다.";
				 redirectAttributes.addFlashAttribute("message", message);
			 } else {
				 redirectAttributes.addFlashAttribute("message", "회원 상태를 변경할 수 없습니다.");
			 }
			 
		} catch (IllegalArgumentException e) {
			// Service에서 잘못된 active 값으로 발생시킨 예외 처리
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
			 
	    // 관리자 회원 목록으로 이동
		 return "redirect:/admin/members";	
	
	}
	
	// 관리자 전용 영수증 이미지 조회
	@GetMapping("/admin/receipts")
	public ResponseEntity<Resource> viewReceipt(@RequestParam("fileName") String fileName){
		
		// 서버에 저장된 영수증 파일 조회
		Resource resource = receiptFileService.loadReceipt(fileName);
		
		// 파일 확장자에 따라 이미지 타입 결정
		String lowerName = fileName.toLowerCase(java.util.Locale.ROOT);
		
		MediaType mediaType;
		
		if (lowerName.endsWith(".png")) {
			mediaType = MediaType.IMAGE_PNG;
		} else if (lowerName.endsWith(".webp")){
			mediaType = MediaType.parseMediaType("image/webp");
		} else {
			// JPG, JPEG
			mediaType = MediaType.IMAGE_JPEG;
		}
		
		// 이미지를 브라우저에 응답
		return ResponseEntity.ok().contentType(mediaType).body(resource);
	}
	
}
































