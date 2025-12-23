package com.bm.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "TAG_CODE")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class TagCode {
	
	@Id
	@Column(name = "TAG_CODE")
	private Long tagCode;

	@Column(name = "TAG_CATEGORY", nullable = false, length = 50)
    private String tagCategory;


	








}
