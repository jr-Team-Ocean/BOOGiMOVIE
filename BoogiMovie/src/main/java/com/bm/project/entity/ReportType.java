package com.bm.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "REPORT_TYPE")
@Getter 
@Setter
public class ReportType {
    
    @Id
    @Column(name = "REPORT_TYPE_CODE")
    private Long reportTypeCode; // 신고 유형 코드 (PK)

    @Column(name = "REPORT_NAME", nullable = false, length = 100)
    private String reportName;   // 신고 유형 이름 (예: 욕설, 부적절한 콘텐츠 등)
}