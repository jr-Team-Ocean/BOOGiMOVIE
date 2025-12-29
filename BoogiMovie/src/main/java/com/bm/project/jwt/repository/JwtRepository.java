package com.bm.project.jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bm.project.entity.Member;

@Repository
public interface JwtRepository extends JpaRepository<Member, Long>{

	Optional<Member> findByMemberId(String adminId);


}
