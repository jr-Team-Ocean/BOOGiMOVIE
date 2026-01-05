package com.bm.project.service.admin;

import com.bm.project.entity.Report;
import java.util.List;

public interface AdminService {

	// 신고 내용 저장하기
	boolean insertReport(Report report);
	
	// 신고 전체 목록 조회 (초기 화면용)
    List<Report> selectReportList();

    // 신고 처리 (삭제/유지 버튼 클릭 시 상태 업데이트)
    int updateReportStatus(Long reportNo, String status);

    // 선택 삭제 (체크박스로 여러 개 삭제 시)
    int deleteSelectedReports(List<Long> reportIds);

}
