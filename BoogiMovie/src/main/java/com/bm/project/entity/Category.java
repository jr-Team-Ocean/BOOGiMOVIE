package com.bm.project.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name="CATEGORY")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Category {
	
	@Id
	@Column(name = "CATEGORY_ID", nullable = false)
	private Long categoryId;
	
	@Column(name = "CATEGORY_NAME", nullable = false, length = 50)
    private String categoryName;
	
	// 부모
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "P_CATEGORY_ID")
    private Category pCategoryId;
	
	
	// 자식
	@OneToMany(mappedBy = "pCategoryId", fetch = FetchType.LAZY)
    private List<Category> children = new ArrayList<>();
	
	
	
	
	
	
	
	
	
	
	
}
