package org.foodreview.visit;

import java.time.LocalDate;
import java.util.List;

import org.foodreview.admin.AdminVisitDTO;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitService {
	
	private final VisitMapper visitMapper;
	
	// 방문 날짜 검증
	public void validateVisitDate(LocalDate visitDate) {
		
		if(visitDate == null) {
			throw new IllegalArgumentException("방문 날짜를 입력해 주세요.");
		}
		if(visitDate.isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("미래 날짜로는 방문 인증을 신청할 수 없습니다.");
		}
	}
	
	// 방문 인증 등록
	public void insertVisit(Visit visit) {
		
		validateVisitDate(visit.getVisitDate());
		
		visitMapper.insertVisit(visit);
		
	}
	
	// 로그인한 회원의 승인된 방문 인증 하나 조회
	public Visit findApprovedVisit(Integer vno, Integer mno) {
		return visitMapper.findApprovedVisit(vno, mno);
	}
	
	// 로그인한 회원의 방문 인증 목록 조회
	public List<Visit> findMyVisits(Integer mno) {
		return visitMapper.findMyVisits(mno);
	}
	
	
	
	// 관리자 : 승인 대기 중인 방문 인증 목록 조회	
	public List<AdminVisitDTO> findPendingVisits(){
		return visitMapper.findPendingVisits();
	}
	
	// 관리자 : 방문 인증 승인
	public int approveVisit (Integer vno) {
		return visitMapper.approveVisit(vno);
	}
	
	// 관리자: 방문 인증 거절
	public int rejectVisit (Integer vno) {
		return visitMapper.rejectVisit(vno);
	}
	
	
	
	
	
}










