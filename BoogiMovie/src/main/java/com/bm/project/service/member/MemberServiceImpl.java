package com.bm.project.service.member;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bm.project.dto.MemberDto;
import com.bm.project.dto.MemberDto.Create;
import com.bm.project.dto.MemberDto.Login;
import com.bm.project.dto.MemberDto.LoginResult;
import com.bm.project.entity.Member;
import com.bm.project.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
	
	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder bcrypt;

	// 회원 등록
	@Override
	public String createMember(Create creatDto) {
		
		// 비밀번호를 BCrypt를 이용하여 암호화
		String encPw = bcrypt.encode(creatDto.getMemberPw());
		creatDto.setMemberPw(encPw);
		
		Member member = creatDto.toEntity();
		
		memberRepository.save(member);
		
		return member.getMemberId();
	}

	// 로그인
	@Override
	public MemberDto.LoginResult login(Login loginDto) {
		
		// 아이디로 회원이 있는지 확인 -> 비밀번호 맞는지 비교, 없으면 null/ 있으면 LoginResult로 반환
		return memberRepository.findByMemberId(loginDto.getMemberId())
				.filter(m -> bcrypt.matches(loginDto.getMemberPw(), m.getMemberPw()))
	            .map(MemberDto.LoginResult::fromEntity) // 성공 시 DTO 변환
	            .orElse(null);  
	}

}
