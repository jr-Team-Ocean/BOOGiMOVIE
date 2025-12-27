package com.bm.project.jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bm.project.entity.Member;

public interface JwtRepository extends JpaRepository<Member, Long>{

	Optional<Member> findByMemberId(String adminId);


}
