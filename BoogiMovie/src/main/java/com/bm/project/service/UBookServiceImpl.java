package com.bm.project.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bm.project.dto.UbookDto;
import com.bm.project.dto.UbookDto.Response;
import com.bm.project.entity.Product;
import com.bm.project.repository.UbookRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UBookServiceImpl implements UbookService{
	
	private final UbookRepository ubookRepository;

	
	// 중고도서 목록 조회
	@Override
	public Page<UbookDto.Response> selectbookList(Map<String, Object> paramMap, Pageable pageable) {
	
		Page<Product> page = ubookRepository.selectbookList(paramMap, pageable);
		
		
		
		return new PageImpl<>(pageable, page.getTotalElements());
	
	
	}
	

}
