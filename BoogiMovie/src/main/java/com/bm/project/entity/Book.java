package com.bm.project.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.bm.project.enums.CommonEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "BOOK")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@DynamicInsert
@DynamicUpdate
@Setter
@ToString
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
