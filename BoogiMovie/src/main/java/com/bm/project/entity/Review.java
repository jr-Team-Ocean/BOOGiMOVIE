package com.bm.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// 신고 관리를 위한 임세 엔티티

@Entity
@Table(name = "REVIEW")
@Getter @Setter
public class Review {
    @Id
    @Column(name = "REVIEW_NO")
    private Long reviewNo;

    @Column(name = "REVIEW_CONTENT")
    private String reviewContent;
}