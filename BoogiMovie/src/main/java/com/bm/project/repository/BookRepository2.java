package com.bm.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bm.project.entity.Book;

public interface BookRepository2 extends JpaRepository<Book, Long>{

	Optional<Book> findByProduct_ProductNo(Long productNo);

	boolean existsByIsbn(String isbn);

}
