package com.bm.project.service.member;

import org.springframework.stereotype.Service;

import com.bm.project.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AjaxServiceImpl implements AjaxService{
	
	private final MemberRepository repository;

	// 아이디 중복검사
	@Override
	public boolean checkId(String memberId) {
		return repository.existsByMemberId(memberId);
	}

	// 닉네임 중복검사
	@Override
	public boolean checkNickname(String memberNickName) {
		return repository.existsByMemberNickName(memberNickName);
	}

	// 전화번호 중복검사
	@Override
	public boolean checkPhone(String memberPhone) {
		return repository.existsByMemberPhone(memberPhone);
	}

	
	
}
