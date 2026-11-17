package org.foodreview.visit;

import org.foodreview.file.ReceiptFileService;
import org.foodreview.member.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class VisitController {
	
	private final VisitService visitService;
	private final ReceiptFileService receiptFileService;
	
	// 방문 인증 화면
	@GetMapping("/visits/register")
	public String visitForm(@RequestParam("pno") Integer pno, Model model) {
		
		model.addAttribute("pno", pno);
		
		return "visit/register";
	}

	// 방문 인증 등록 처리
	@PostMapping("/visits/register")
	public String register(Visit visit, @RequestPart("receiptFile") MultipartFile receiptFile, HttpSession session, Model model) {
		
		Member loginMember = (Member)session.getAttribute("member");
		visit.setMno(loginMember.getMno());
		
		// 저장된 영수증 파일명을 기억할 변수
		String savedName = null;
		
		try {
			
			// 1. 방문 날짜부터 검사
		    visitService.validateVisitDate(visit.getVisitDate());
		    
		    // 2. 영수증 파일 저장
			savedName = receiptFileService.saveReceipt(receiptFile);
			
			// 3. DB에 저장할 파일명 설정
			visit.setReceipt(savedName);
			
			// 4. 방문 인증 DB 등록
			visitService.insertVisit(visit);
			
		} catch(Exception e) {
			
			// 파일은 저장됐는데 이후 작업이 실패했다면 파일 삭제
			if (savedName != null) {
				try {
					receiptFileService.deleteReceipt(savedName);
				} catch (Exception deleteException) {
					// 삭제 실패 사실도 콘솔에 남김
					System.err.println("영수증 파일 정리 실패: " + deleteException.getMessage());
				}
			}
			// 기존 오류 화면으로 돌아가기
			model.addAttribute("error", 
					e instanceof IllegalArgumentException ? e.getMessage() : "방문 인증 등록 중 오류가 발생했습니다.");
			model.addAttribute("visit", visit);
			model.addAttribute("pno", visit.getPno());
			
			return "visit/register";
		}
		
		return "redirect:/mypage";
	} 
	
	
	
	
	
	
	
	
	
	
	
	
	
}
