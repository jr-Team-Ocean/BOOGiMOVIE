package com.bm.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bm.project.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long>, MovieRepositoryCustom {

	
}
