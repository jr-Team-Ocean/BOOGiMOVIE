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
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "REPORT")
@Getter 
@Setter
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_REPORT_NO")
    @SequenceGenerator(name = "SEQ_REPORT_NO", sequenceName = "SEQ_REPORT_NO", allocationSize = 1)
    @Column(name = "REPORT_NO")
    private Long reportNo;        // 신고 번호 (PK)

    @Column(name = "REPORT_REASON", nullable = false)
    private String reportReason;  // 사용자가 직접 입력한 상세 사유

    @Column(name = "REPORT_DATE", nullable = false)
    private LocalDateTime reportDate; // 신고된 날짜

    @Column(name = "RESULT_DATE")
    private LocalDateTime resultDate; // 처리 완료된 날짜

    @Column(name = "REPORT_RESULT", length = 1)
    private String reportResult;  // 처리 결과 (Y: 처리, N: 유지)

    // [연관관계 1] 어떤 유형의 신고인가? (REPORT_TYPE 테이블 참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_TYPE")
    private ReportType reportType;

    // [연관관계 2] 어떤 리뷰에 대한 신고인가? (REVIEW 테이블 참조)
    // 이 부분이 있어야 '리뷰 본문'을 화면에 가져올 수 있습n니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEW_NO")
    private Review review;

    @Column(name = "MEMBER_NO")
    private Long memberNo;        // 신고한 회원 번호
}