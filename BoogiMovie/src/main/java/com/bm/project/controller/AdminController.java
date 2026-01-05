package com.bm.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bm.project.entity.Report;

import com.bm.project.service.admin.AdminService;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final AdminService service;

	// 통계 화면 보여주기
	@GetMapping("/statistics")
	public String statistics(Model model) {
		model.addAttribute("activeMenu", "statistics");
		return "admin/statistics";
	}
	
//	// 신고 작성 페이지(Form) 열기
//    @GetMapping("/form")
//    public String showReportForm(@RequestParam("reviewNo") Long reviewNo, Model model) {
//        
//    	System.out.println("신고 페이지 요청 - 리뷰 번호: " + reviewNo);
//
//        model.addAttribute("targetReviewNo", reviewNo);
//        model.addAttribute("userName", "하멍멍"); // 임시데이터
//
//        return "admin/report_form"; 
//    }
//
//    // 신고 데이터 DB 저장 후 관리페이지 이동
//    @PostMapping("/submit")
//    public String submitReport(@ModelAttribute Report report, RedirectAttributes ra) {
//        
//    	System.out.println("신고 접수 시도 - 리뷰 번호:" + report.getReview().getReviewNo());
//
//        boolean result = service.insertReport(report);
//        
//        if(result) {
//            ra.addFlashAttribute("message", "신고가 성공적으로 접수되었습니다.");
//        } else {
//            ra.addFlashAttribute("message", "신고 접수에 실패하였습니다.");
//        }
//        
//        return "redirect:/admin/report/list"; 
//    }
//    
//    // 신고 목록 조회 (메인 관리 페이지)
//    @GetMapping("/list")
//    public String selectReportList(Model model, 
//                                   @RequestParam(value = "page", defaultValue = "1") int page) {
//        
//        // 서비스에서 전체 신고 내역 가져오기
//        List<Report> reportList = service.selectReportList();
//        
//        model.addAttribute("reportList", reportList);
//        model.addAttribute("currentPage", page);
//        
//        // 날짜 데이터가 없을 경우를 대비한 임시 값 (실제로는 서비스에서 계산 권장)
//        model.addAttribute("startDate", "2025.09.01");
//        model.addAttribute("endDate", "2025.09.21");
//
//        return "admin/review_report"; // review_report.html로 이동
//    }
//
//    // 신고 처리 (삭제 또는 유지 버튼 클릭 시)
//
//    @PostMapping("/process")
//    @ResponseBody // 비동기(AJAX) 처리를 원하실 경우 사용, 페이지 이동이면 삭제 가능
//    public String processReport(@RequestParam("reportNo") Long reportNo,
//                                @RequestParam("result") String result,
//                                RedirectAttributes ra) {
//        
//        int updateResult = service.updateReportStatus(reportNo, result);
//        
//        if(updateResult > 0) {
//            ra.addFlashAttribute("message", "처리가 완료되었습니다.");
//        } else {
//            ra.addFlashAttribute("message", "처리 중 오류가 발생했습니다.");
//        }
//        
//        return "redirect:/admin/report/list";
//    }
//
//    // 선택 삭제 (체크박스 전체 삭제 버튼)
//    @PostMapping("/deleteSelected")
//    public String deleteSelected(@RequestParam("reportIds") List<Long> reportIds, 
//                                 RedirectAttributes ra) {
//        
//        int deleteCount = service.deleteSelectedReports(reportIds);
//        
//        ra.addFlashAttribute("message", deleteCount + "건의 내역이 삭제되었습니다.");
//        
//        return "redirect:/admin/report/list";
//    }
    
	
}
