package com.bm.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bm.project.entity.MemberSocial;
import com.bm.project.enums.CommonEnums.SocialProvider;

public interface SocialRepository extends JpaRepository<MemberSocial, Long>{

	Optional<MemberSocial> findByProviderAndSocialId(SocialProvider provider, String socialId);
    boolean existsByMember_MemberNoAndProvider(Long memberNo, SocialProvider provider);
}
