package com.bm.project.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.bm.project.enums.CommonEnums;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="PRODUCT")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@DynamicInsert
@DynamicUpdate
@Setter
@ToString
public class Product {
	
	@Id
	@Column(name="PRODUCT_NO", length=20, nullable = false)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "product_seq")
	@SequenceGenerator(name = "product_seq", sequenceName = "SEQ_PRODUCT_NO", initialValue = 1, allocationSize = 1)
	private Long productNo;
	
	@Column(name="PRODUCT_TITLE", length=300, nullable=false)
	private String productTitle;
	
	@Lob
	@Column(name="PRODUCT_CONTENT", nullable=false)
	private String productContent;
	
	@Column(name="PRODUCT_DATE", nullable=false)
	private LocalDateTime productDate;
		
	
	@Column(name="PRODUCT_PRICE", nullable=false)
	private Integer productPrice;
	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "PRODUCT_DEL_FL", columnDefinition = "CHAR(1)", nullable = false)
	private CommonEnums.ProductDelFl productDelFl;
	
	
	@Column(name="IMG_PATH", length=500, nullable=false)
	private String imgPath;
	
	@jakarta.persistence.Transient
	private String authorName;
	
	
	
	@PrePersist
	// @PrePersist : JPA가 INSERT 하기 직전에 자동으로 실행되는 메소드
	public void prePersist() {
		
		// 날짜 미지정시 오늘날짜로
		if(this.productDate == null) {
			this.productDate = LocalDateTime.now();
		}
		
		if (this.productDelFl == null) {
			this.productDelFl = CommonEnums.ProductDelFl.N;
			
		}
		
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CATEGORY_ID", nullable = false)
	private Category category;
	
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	@Builder.Default
	private List<ProductTagConnect> productTagConnects = new ArrayList<>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TYPE_CODE", nullable = false)
	private ProductType productType;
	
	
	
	
	
	
	
}
