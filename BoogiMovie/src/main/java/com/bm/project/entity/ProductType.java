package com.bm.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="PRODUCT_TYPE")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
public class ProductType {
	
	@Id
	@Column(name = "TYPE_CODE", nullable = false)
	private Long typeCode;
	
	@Column(name = "TYPE_NAME", length = 30 ,nullable = false)
	private String typeName;
	
	
	
	
	
}
