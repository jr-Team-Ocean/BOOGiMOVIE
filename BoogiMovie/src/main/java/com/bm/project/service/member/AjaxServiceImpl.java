package com.bm.project.service.member;

import org.springframework.stereotype.Service;

import com.bm.project.dto.MemberDto.DupCheckResponse;
import com.bm.project.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AjaxServiceImpl implements AjaxService{
	
	private final MemberRepository repository;

	@Override
	public boolean checkId(String memberId) {
		return repository.existsByMemberId(memberId);
	}

	@Override
	public boolean checkNickname(String memberNickName) {
		return repository.existsByMemberNickName(memberNickName);
	}

	
	
}
