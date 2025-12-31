package com.bm.project.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bm.project.dto.UbookDto;
import com.bm.project.dto.UbookDto.Response;

public interface UbookService {

	Page<UbookDto.Response> selectbookList(Map<String, Object> paramMap, Pageable pageable);
	
	
	
	
	
	
	
	


}
