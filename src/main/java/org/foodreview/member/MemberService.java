package org.foodreview.member;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	
	private final MemberMapper memberMapper;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//회원가입
	public int register(Member member) {
		
		String encodedPwd = bCryptPasswordEncoder.encode(member.getPwd());
		member.setPwd(encodedPwd);
		
		return memberMapper.register(member);
	}
	
	
	//로그인
	public Member login(Member member) {
		Member dbMember = memberMapper.findByEmail(member.getEmail());
		
		if (dbMember == null) {
			return null;
		}	
		if (bCryptPasswordEncoder.matches(member.getPwd(), dbMember.getPwd())) {
			return dbMember;	
		}
		
		return null;				
	}
	
	
	// 회원번호로 회원조회
	public Member findByMno(Integer mno) {
		return memberMapper.findByMno(mno);
	}
	
	// 회원정보 수정
	public int updateMember(Member member) {
		return memberMapper.updateMember(member);
	}
	
	// 닉네임 중복 확인 (본인 제외)
	public int countNicknameForUpdate(String nickname, Integer mno) {
		return memberMapper.countNicknameForUpdate(nickname, mno);
	}
	
	// 전화번호 중복 확인 (본인 제외)
	public int countPhoneForUpdate(String phone, Integer mno) {
		return memberMapper.countPhoneForUpdate(phone, mno);
	}
	
	// 관리자 : 전체 회원 조회
	public List<Member> findAllMembers(){
		return memberMapper.findAllMembers();
	}
	
	// 관리자: 회원 상태 변경
	public int updateMemberActive(Integer mno, Integer active) {
		
		if (active == null || (active != 0 && active != 1)) {
			throw new IllegalArgumentException("잘못된 회원 상태입니다.");
		}
		
		return memberMapper.updateMemberActive(mno, active);
	}
	
	
}





















