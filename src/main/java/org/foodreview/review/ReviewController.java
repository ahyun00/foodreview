package org.foodreview.review;

import java.util.List;

import org.foodreview.file.ReviewImageFileService;
import org.foodreview.member.Member;
import org.foodreview.visit.Visit;
import org.foodreview.visit.VisitService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReviewController {
	
	private final ReviewService reviewService;
	private final VisitService visitService;
	private final ReviewImageFileService reviewImageFileService;
	
	// 리뷰 작성 화면
	@GetMapping("/reviews/write")
	public String writeForm(@RequestParam("vno") Integer vno, Model model, HttpSession session) {
		
		 // 인터셉터가 로그인 검사를 완료했으므로
	    // 세션에서 로그인 회원 정보만 가져옴
		Member loginMember = (Member)session.getAttribute("member");
		
		// 로그인한 회원의 승인된 방문 인증인지 확인
		Visit visit = visitService.findApprovedVisit(vno, loginMember.getMno());
		
		// 본인의 승인된 방문 인증이 아니면 메인으로 이동
		if (visit == null) {
			return "redirect:/";
		}
		
		// 해당 방문 인증으로 이미 리뷰를 작성했는지 확인
		if (reviewService.hasReview(vno)) {
			return "redirect:/mypage";
		}
		
		// 리뷰 작성 화면에 방문 인증 번호 전달
		model.addAttribute("vno", vno);
		
		return "review/write";
	}
	
	
	// 리뷰 등록 처리 
	@PostMapping("/reviews/write")
	public String write(
			Review review,
			@RequestPart(value = "reviewFile", required = false) MultipartFile reviewFile,
			HttpSession session, 
			Model model) {
		
		 // 인터셉터에서 로그인 검사를 마쳤으므로
	    // 세션에서 로그인한 회원 정보만 가져옴
		Member loginMember = (Member) session.getAttribute("member");
			
		// 본인의 승인된 방문인지 확인
		Visit visit = 
				visitService.findApprovedVisit(review.getVno(), loginMember.getMno());
		
		if (visit == null) {
			return "redirect:/";
		}
		
		// 중복 리뷰 등록 방지
		if (reviewService.hasReview(review.getVno())) {
			return "redirect:/mypage";
		}
		
		// 파일 저장 여부를 기억하기 위한 변수
	    String savedName = null;
		
		try {
			// 리뷰 내용과 별점을 먼저 검증
			if (review.getRating() == null
					|| review.getRating() < 1
					|| review.getRating() >5) {
				throw new IllegalArgumentException(
						"별점은 1~5점만 가능합니다.");
			}
			
			if (review.getContent() == null
					|| review.getContent().isBlank()) {
				throw new IllegalArgumentException(
	                    "리뷰 내용을 입력해 주세요.");
			}
			
			// 이미지가 있으면 저장, 없으면 null 반환
			savedName = reviewImageFileService.saveImage(reviewFile);
			
			// DB에 저장할 이미지 파일명 설정
			review.setReviewImage(savedName);
			
			// 리뷰 DB 등록
			reviewService.insertReview(review);
			
		} catch (Exception e) {
			
			// 파일 저장 후 DB 등록에 실패한 경우 파일 정리
			if (savedName != null) {
				try {
					reviewImageFileService.deleteImage(savedName);
				} catch (Exception deleteException) {
					System.err.println(
							"리뷰 이미지 정리 실패: "
							+ deleteException.getMessage());
				}
			}
			
			// 오류 메시지 표시
			model.addAttribute("error", e instanceof DuplicateKeyException
					? "이미 리뷰가 등록된 방문입니다." : e instanceof IllegalArgumentException
							? e.getMessage() : "리뷰 등록 중 오류가 발생했습니다.");
			
			model.addAttribute("review", review);
			model.addAttribute("vno", review.getVno());
			
			return "review/write";
		}
		
	
			return "redirect:/";
	}
	
	
	// 리뷰 목록 페이징 조회
	@GetMapping("/reviews")
	public String reviewList(@RequestParam(name= "page", defaultValue = "1") int page, Model model) {
		
		if ( page < 1) { 
			page = 1;
			}	
		
		 // 해당 페이지에 표시할 리뷰 목록 조회
		List<Review> reviews = reviewService.findByPage(page);
		
		// 전체 리뷰 개수 조회
		int totalCount = reviewService.countAll();
		
		// 전체 페이지 수 계산
		int totalPages = (int) Math.ceil(totalCount / 10.0);
		
		// HTML에 데이터 전달
		model.addAttribute("reviews", reviews);
		model.addAttribute("page", page);
		model.addAttribute("totalPages", totalPages);
		
		return "review/list";		
	}
	
	
	// 리뷰 상세 조회
	@GetMapping("/reviews/{rno}")
	public String reviewDetail(@PathVariable("rno") Integer rno, Model model, HttpSession session) {
		
		// 리뷰 번호로 리뷰 하나 조회
		Review review = reviewService.findByRno(rno);
		
		// 존재하지 않는 리뷰면 목록으로 이동
		if (review == null ) {
			return "redirect:/reviews";
		}
		
		model.addAttribute("review", review);
		
		// 기본적으로 수정·삭제 권한 없음
		boolean canEdit = false;
		
		// 로그인한 회원 확인
		Member loginMember = (Member)session.getAttribute("member");
		
		if (loginMember != null) {
			// 리뷰가 로그인 회원의 승인된 방문에 속하는지 확인
			Visit visit = visitService.findApprovedVisit(
					review.getVno(), loginMember.getMno()
			);
			
			// 본인 리뷰인 경우에만 true
			canEdit = (visit !=null);
		}
		model.addAttribute("canEdit", canEdit);
		
		return "review/detail";
	}
	
	
	// 리뷰 수정 화면
	@GetMapping("/reviews/{rno}/edit")
	public String editForm(@PathVariable("rno") Integer rno, Model model, HttpSession session) {
		
		 // 인터셉터에서 로그인 검사를 완료했으므로
	    // 세션에서 로그인 회원 정보만 가져옴
		Member loginMember = (Member) session.getAttribute("member");
		
		// 수정하려는 리뷰를 DB에서 조회
		Review review = reviewService.findByRno(rno);
		
		// 존재하지 않는 리뷰 번호라면 리뷰 목록으로 이동
	    if (review == null) {
	        return "redirect:/reviews";
	    }
		
		// 이 리뷰의 방문 인증이 로그인한 회원의 것인지 확인
		Visit visit = visitService.findApprovedVisit(review.getVno(), loginMember.getMno());
		
		// 본인의 리뷰가 아니면 수정 화면에 들어갈 수 없음
		if (visit == null) {
			return "redirect:/reviews/" +rno;
		}
		
		model.addAttribute("review", review);
		return "review/edit";
	}
	
	// 리뷰 수정 처리
	@PostMapping("/reviews/{rno}/edit")
	public String edit(
			@PathVariable("rno") Integer rno, 
			Review review, 
			@RequestPart(value = "reviewFile", required = false) MultipartFile reviewFile,
			HttpSession session, 
			Model model) {
		
		// 인터셉터에서 로그인 검사를 완료했으므로
	    // 세션에서 로그인한 회원 정보만 가져옴
		Member loginMember = (Member) session.getAttribute("member");
		
		// DB에서 기존 리뷰 조회
		Review existingReview = reviewService.findByRno(rno);
		
	    if (existingReview == null) {
	        return "redirect:/reviews";
	    }
		
		// 기존 리뷰의 방문 인증이 로그인한 회원의 것인지 확인
		Visit visit = visitService.findApprovedVisit(existingReview.getVno(), loginMember.getMno());
		
		if(visit == null) {
			return "redirect:/reviews/"+ rno;
		}
		
		// 기존 리뷰 번호와 방문 인증 번호 유지
		review.setRno(rno);
		review.setVno(existingReview.getVno());
		
		// 기존 이미지 파일명
		String oldImage = existingReview.getReviewImage();
		
		// 새 이미지 파일명
		String newImage = null;
		
		try {
			// 새 이미지를 선택한 경우에만 저장
			if (reviewFile != null && !reviewFile.isEmpty()) {
				newImage = reviewImageFileService.saveImage(reviewFile);
			}
			
			// 새 이미지가 있으면 교체, 없으면 기존 이미지 유지
			review.setReviewImage(newImage != null ? newImage : oldImage);
			
			// 리뷰 DB 수정
			int updated = reviewService.updateReview(review);
			
			if (updated != 1) {
				throw new IllegalStateException("리뷰 수정에 실패했습니다.");
			}
			
		} catch (Exception e) {
			
			// DB 수정 실패 시 새로 저장한 이미지 삭제
			if (newImage != null) {
				try {
					reviewImageFileService.deleteImage(newImage);
					
				} catch (Exception deleteException) {
					System.err.println(
							"새 이미지 정리 실패: " + deleteException.getMessage());
				}
			}
			
			model.addAttribute("error", 
					e instanceof IllegalArgumentException 
					? e.getMessage() : "리뷰 수정 중 오류가 발생했습니다.");
			
			// 오류 화면에 기존 이미지 표시
			review.setReviewImage(oldImage);
            model.addAttribute("review", review);

            return "review/edit";
		}
		
		// 새 이미지로 교체했다면 이전 이미지 삭제
		if (newImage != null && oldImage != null) {
			try {
				reviewImageFileService.deleteImage(oldImage);
				
			} catch (Exception e) {
				System.err.println("기존 이미지 삭제 실패: " + e.getMessage());
			}
		}		
		
		return "redirect:/reviews/" +rno;
	}
	
	
	// 리뷰 삭제
	@PostMapping("/reviews/{rno}/delete")
	public String delete(@PathVariable("rno") Integer rno, HttpSession session) {
		
		// 인터셉터에서 로그인 검사를 완료했으므로
	    // 세션에서 로그인한 회원 정보만 가져옴
		Member loginMember = (Member) session.getAttribute("member");
		
		// 삭제하려는 리뷰를 DB에서 조회
		Review review = reviewService.findByRno(rno);
		
		// 존재하지 않는 리뷰라면 리뷰 목록으로 이동
	    if (review == null) {
	        return "redirect:/reviews";
	    }
		
		// 이 리뷰가 로그인한 회원의 리뷰인지 확인
		Visit visit = visitService.findApprovedVisit(review.getVno(), loginMember.getMno());
		
		if (visit == null) {
			return "redirect:/reviews/" +rno;
		}
		
		// 삭제할 이미지 파일명 기억
		String savedName = review.getReviewImage();
		
		// DB에서 리뷰 삭제
		int deleted = reviewService.deleteReview(rno);
		
		// DB 삭제에 성공했고 이미지가 있다면 파일 삭제
		if (deleted == 1 && savedName != null) {
			
			try {
				reviewImageFileService.deleteImage(savedName);
				
			} catch (Exception e) {
			
			System.err.println("리뷰 이미지 삭제 실패: " + e.getMessage());
			
			}
		}
		
		return "redirect:/reviews";
		
	}
	

}







































