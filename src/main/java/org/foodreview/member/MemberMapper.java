package org.foodreview.member;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {
	
	// 회원가입
	// Member 객체를 member 라는 이름으로 Mapper.xml에 전달
    // INSERT된 행의 개수를 반환 (정상 등록 시 보통 1)
	int register(@Param("member") Member member);
	
	// 회원 한명 조회
	Member findByEmail(@Param("email") String email);
	
	// 회원번호로 회원 조회
	Member findByMno(@Param("mno") Integer mno);
	
	// 회원정보 수정
	int updateMember(@Param("member") Member member);
	
	// 다른 회원이 같은 닉네임을 사용하는지 확인
	int countNicknameForUpdate(@Param("nickname") String nickname, @Param("mno") Integer mno);
	
	// 다른 회원이 같은 전화번호를 사용하는지 확인
	int countPhoneForUpdate(@Param("phone") String phone, @Param("mno") Integer mno);
	
	// 관리자 : 전체 회원 목록 조회
	List<Member> findAllMembers();
	
	// 관리자 : 회원 활성화 / 비활성화
	int updateMemberActive(@Param("mno") Integer mno, @Param("active") Integer active);
	
	
	
	
}





















