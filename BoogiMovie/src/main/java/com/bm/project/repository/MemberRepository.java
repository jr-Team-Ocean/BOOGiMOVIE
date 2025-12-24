package com.bm.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bm.project.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{

	// 아이디 중복검사
	boolean existsByMemberId(String memberId);

	// 닉네임 중복검사
	boolean existsByMemberNickName(String memberNickName);

	// 전화번호 중복검사
	boolean existsByMemberPhone(String memberPhone);

}
