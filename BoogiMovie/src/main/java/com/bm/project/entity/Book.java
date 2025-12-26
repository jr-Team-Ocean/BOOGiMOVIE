package com.bm.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "BOOK")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {
	
	
	@Id
    @Column(name = "PRODUCT_NO")
    private Long productNo;
	
	@OneToOne(fetch = FetchType.LAZY)
    @MapsId // PK 이면서 FK 일 때 사용
    @JoinColumn(name = "PRODUCT_NO")
	private Product product;
	
	
	@Column(name = "BOOK_COUNT")
	private Integer bookCount;
	
	
	@Column(name = "ISBN", length = 20)
	private String isbn;
	
	
	
	
	
	
	
	
	

}
