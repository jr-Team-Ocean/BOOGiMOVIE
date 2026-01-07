package com.bm.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bm.project.entity.Book;
import com.bm.project.entity.Product;
import com.bm.project.entity.Ubook;

public interface UbookRepository2 extends JpaRepository<Ubook, Long>{
	
	Optional<Ubook> findByProduct_ProductNo(Long productNo);

}
