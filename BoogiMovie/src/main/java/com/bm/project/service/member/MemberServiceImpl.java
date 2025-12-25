package com.bm.project.service.member;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bm.project.dto.MemberDto.Create;
import com.bm.project.entity.Member;
import com.bm.project.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
	
	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder bCrypt;

	// 회원 등록
	@Override
	public String createMember(Create creatDto) {
		
		// 비밀번호를 BCrypt를 이용하여 암호화
		String encPw = bCrypt.encode(creatDto.getMemberPw());
		creatDto.setMemberPw(encPw);
		
		Member member = creatDto.toEntity();
		
		memberRepository.save(member);
		
		return member.getMemberId();
	}

}
