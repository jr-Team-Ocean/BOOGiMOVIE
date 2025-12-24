package com.bm.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bm.project.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{

	boolean existsByMemberId(String memberId);

	boolean existsByMemberNickName(String memberNickName);

}
