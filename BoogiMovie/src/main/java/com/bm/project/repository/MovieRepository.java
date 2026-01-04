package com.bm.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bm.project.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long>, MovieRepositoryCustom {

	// 영화 상세정보
	@EntityGraph(attributePaths = {"product", "product.productTagConnects", "product.productTagConnects.productTag"})
	Optional<Movie> findById(Long productNo);
}
