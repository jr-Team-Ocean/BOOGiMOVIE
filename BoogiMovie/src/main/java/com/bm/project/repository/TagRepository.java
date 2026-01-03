package com.bm.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bm.project.entity.ProductTag;
import com.bm.project.entity.TagCode;

public interface TagRepository extends JpaRepository<ProductTag, Long>{

	Optional<ProductTag> findByTagNameAndTagCode(String writer, TagCode wCode);

}
