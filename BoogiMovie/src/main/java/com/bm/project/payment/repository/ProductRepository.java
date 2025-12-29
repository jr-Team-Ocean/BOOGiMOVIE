package com.bm.project.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bm.project.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
