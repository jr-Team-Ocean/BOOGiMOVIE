package com.bm.project.elasticsearch;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CopyToDocumentController {
	
	private final CopyToDocumentService service;
	
	// 오라클에 있는 데이터 -> 엘라스틱 인덱스로 그대로 복사
	@GetMapping("/admin/sync")
	@ResponseBody
	public String syncData() {
		service.syncAllData();
		return "데이터 삽입 성공";
	}

}
