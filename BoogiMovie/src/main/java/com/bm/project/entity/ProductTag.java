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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "PRODUCT_TAG")
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
	
	
	@Column(name = "TAG_NAME", length = 50, nullable = false, unique = true)
	// 유일 = unique
	private String tagName;
	
	
	
	
}
