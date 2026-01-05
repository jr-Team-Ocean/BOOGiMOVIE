package com.bm.project.entity;

import java.time.LocalDateTime;

import com.bm.project.enums.CommonEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// 신고 관리를 위한 임세 엔티티

@Entity
@Table(name = "REVIEW")
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_REVIEW_NO")
    @SequenceGenerator(name = "SEQ_REVIEW_NO", sequenceName = "SEQ_REVIEW_NO", allocationSize = 1)
    @Column(name = "REVIEW_NO")
    private Long reviewNo;

    @Column(name = "REVIEW_SCORE", nullable = false)
    private Integer reviewScore;
    
    
    @Column(name = "REVIEW_CONTENT")
    private String reviewContent;
    
    @Column(name = "REVIEW_TIME", nullable = false)
    private LocalDateTime reviewTime;
    
    @Column(name = "PRODUCT_NO", nullable = false)
    private Long productNo;
    
    @Column(name = "MEMBER_NO", nullable = false)
    private Long memberNo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_NO", insertable = false, updatable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_NO", insertable = false, updatable = false)
    private Member member;
    
    
	@PrePersist
	public void prePersist() {		
		// 날짜 미지정시 오늘날짜로
		if(this.reviewTime == null) {
			this.reviewTime = LocalDateTime.now();
		}		
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
}