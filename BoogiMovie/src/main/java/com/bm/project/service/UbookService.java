package com.bm.project.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bm.project.dto.UbookDto;
import com.bm.project.dto.UbookDto.Response;

public interface UbookService {

	
	// 중고도서 목록 조회
	Page<UbookDto.Response> selectbookList(Map<String, Object> paramMap, Pageable pageable);

	// 중고도서 상세 조회
	UbookDto.Response selectUbookDetail(Long productNo);
	
	
	
	
	
	
	
	


}
