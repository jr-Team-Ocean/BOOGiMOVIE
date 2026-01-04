package com.bm.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "PRODUCT_TAG",
		uniqueConstraints = @UniqueConstraint(name = "UK_TAG_CODE_NAME",
	    columnNames = {"TAG_CODE", "TAG_NAME"} // 태그 종류별로 같은 이름은 가능
	  ))
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductTag {
	
	@Id
	@Column(name = "TAG_NO", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_seq")
	@SequenceGenerator(name = "tag_seq", sequenceName = "seq_tag_no", allocationSize = 1)
	private Long tagNo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TAG_CODE", nullable = false)
	private TagCode tagCode;
	
	
	@Column(name = "TAG_NAME", length = 50, nullable = false)
	// 유일 = unique
	private String tagName;
	
	
	
	
}
