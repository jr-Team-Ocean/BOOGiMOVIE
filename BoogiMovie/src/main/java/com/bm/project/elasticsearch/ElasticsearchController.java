package com.bm.project.elasticsearch;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import oracle.jdbc.proxy.annotation.Post;

@Controller
@RequiredArgsConstructor
public class ElasticsearchController {
	
	private final ElasticsearchService service;
	
	// 오라클에 있는 데이터 -> 엘라스틱 인덱스로 그대로 복사
	@GetMapping("/admin/sync")
	@ResponseBody
	public String syncData() {
		service.syncAllData();
		return "데이터 삽입 성공";
	}
	
	// 통합검색
	@GetMapping("/search")
	@ResponseBody
	public HeaderSearchDto headerSearch(@RequestParam("query") String query) {
		if (query == null || query.isBlank()) {
            return new HeaderSearchDto();
        }
		
		List<ProductDocument> docs = service.headerSearch(query);
		
		HeaderSearchDto searchDto = new HeaderSearchDto();

		// DTO 안에서 작가, 감독 알아서 나눠줌!
        for (ProductDocument doc : docs) {
        	searchDto.result(doc);
        }

        return searchDto;
	}

}
