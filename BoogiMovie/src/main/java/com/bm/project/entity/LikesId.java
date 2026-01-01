package com.bm.project.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 복합 식별자를 위한 ID 클래스
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // 필수: 복합키 비교를 위해 필요
public class LikesId implements Serializable {
	// Likes 엔티티의 필드명과 일치해야 함
	private Long member;  
    private Long product;
}