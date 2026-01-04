package com.bm.project.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.bm.project.enums.CommonEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="UBOOK")
@Getter
@Setter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class Ubook {
	
	@Id
	@Column(name = "PRODUCT_NO")
	private Long productNo; // FK이면서 PK 역할

	@Column(name="UBOOK_STATUS", length=6, nullable=false)
	private String ubookStatus;
	
	@Column(name="NBOOK_PRICE", nullable=false)
	private Long nbookPrice;
	
	@Lob
	@Column(name="UBOOK_INDEX", nullable=false)
	private String ubookIndex;
	
	@Lob
	@Column(name="AUTHOR_INTRO", nullable=false)
	private String authorIntro;
	
	@MapsId // PK이면서 FK일 때 사용 
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PRODUCT_NO")
	private Product product;
	 
	
}
