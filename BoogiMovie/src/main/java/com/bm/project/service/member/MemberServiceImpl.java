package com.bm.project.service.member;

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

	// 회원 등록
	@Override
	public String createMember(Create creatDto) {
		
		Member member = creatDto.toEntity();
		
		memberRepository.save(member);
		
		return member.getMemberId();
	}

}
