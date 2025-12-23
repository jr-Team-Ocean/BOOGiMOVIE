package com.bm.project.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bm.project.entity.Member;

public interface AdminRepository extends JpaRepository<Member, Long>{

}
