package org.foodreview.visit;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.foodreview.admin.AdminVisitDTO;

@Mapper
public interface VisitMapper {
	
	// 방문 인증 등록
	int insertVisit(@Param("visit") Visit visit);
	
	// 로그인한 회원의 승인된 방문 인증인지 확인
	Visit findApprovedVisit(@Param("vno") Integer vno, @Param("mno") Integer mno );
	
	// 로그인한 회원의 방문 인증 목록 조회
	List<Visit> findMyVisits(@Param("mno") Integer mno);
	
	// 관리자 방문 인증 목록 조회
	List<AdminVisitDTO> findPendingVisits();
	
	// 관리자 : 방문 인증 승인
	int approveVisit(@Param("vno") Integer vno);
	
	// 관리자 : 방문 인증 거절 
	int rejectVisit(@Param("vno") Integer vno);
}
