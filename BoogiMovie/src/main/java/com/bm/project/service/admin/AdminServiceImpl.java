package com.bm.project.service.admin;

import com.bm.project.entity.Report;
import com.bm.project.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class) // 모든 예외 발생 시 롤백
public class AdminServiceImpl implements AdminService {

    private final AdminRepository repository;

    /**
     * 1. 신고 내용 저장하기
     */
    @Override
    public boolean insertReport(Report report) {
        // 필수 기본값 설정
        report.setReportDate(LocalDateTime.now()); // 신고일
        report.setReportResult("N");               // 처리 상태 (N: 미처리)
        
        Report savedReport = repository.save(report);
        return savedReport != null;
    }

    /**
     * 2. 신고 전체 목록 조회
     */
    @Override
    @Transactional(readOnly = true) // 조회 성능 최적화
    public List<Report> selectReportList() {
        // 최신순으로 정렬하고 싶다면 repository.findAll(Sort.by(Direction.DESC, "reportNo")) 사용 가능
        return repository.findAll();
    }

    /**
     * 3. 신고 처리 (상태 업데이트)
     * @param status 'Y' 또는 'N'
     */
    @Override
    public int updateReportStatus(Long reportNo, String status) {
        Optional<Report> optionalReport = repository.findById(reportNo);
        
        if (optionalReport.isPresent()) {
            Report report = optionalReport.get();
            report.setReportResult(status); // 상태 변경 (JPA 더티체킹으로 자동 업데이트)
            // 필요하다면 report.setResultDate(LocalDateTime.now()); 추가
            return 1; // 성공
        }
        return 0; // 내역 없음
    }

    /**
     * 4. 선택 삭제 (여러 건 동시 삭제)
     */
    @Override
    public int deleteSelectedReports(List<Long> reportIds) {
        try {
            // JPA에서 제공하는 ID 리스트 기반 일괄 삭제 기능
            repository.deleteAllById(reportIds);
            return reportIds.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}