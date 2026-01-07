package com.bm.project.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.ToString;

// 페이지 처리용

@ToString

@Getter
public class PageDto<T> {
	private List<T> content; 	 // 데이터 목록
	private int currentPage; 	 // 현재 페이지 번호
	private int totalPage; 	 	 // 전체 페이지 수
	private long totalCount; 	 // 전체 데이터 수
	private boolean hasNext; 	 // 다음 페이지 여부
	private boolean hasPrevious; // 이전 페이지 여부
	
	private int pageSize = 10;     // 하단 페이지 표시 개수 (10개 페이지 = 1 블록)
	
	private int blockStart;        // 현재 블록 시작 페이지
    private int blockEnd;          // 현재 블록 끝 페이지
	
	private int startPage;         // 처음으로 (1페이지)
    private int endPage;           // 끝으로 (마지막 페이지)
    
    private int prevPage;          // 이전 (이전블록 끝 페이지)
    private int nextPage;          // 다음 (다음블록 첫 페이지)
	
	
	
	
	// Page<T> : 페이지 결과를 담고 있는 인터페이스
	public PageDto(Page<T> page) {
		this.content = page.getContent(); 	 		// 데이터 목록
		this.currentPage = page.getNumber() + 1; 		// 현재 페이지
		this.totalPage = page.getTotalPages(); 		// 총 페이지 수
		this.totalCount = page.getTotalElements();  // 총 데이터 수
		this.hasNext = page.hasNext(); 		   		// 다음 페이지 여부
		this.hasPrevious = page.hasPrevious(); 		// 이전 페이지 여부
		
		pagination();
		
	}
	
	// 채팅용 MyBatis 페이지네이션
    public PageDto(List<T> content, int currentPage, int totalCount, int limit) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalCount = (long)totalCount; 
        
        // 전체 페이지 수 직접 계산
        this.totalPage = (int) Math.ceil((double) totalCount / limit);
        
        // 다음/이전 페이지 존재 여부 계산
        this.hasNext = this.currentPage < this.totalPage;
        this.hasPrevious = this.currentPage > 1;
                 
        pagination();
    }
	
	private void pagination() {
		
		this.startPage = 1;
		this.endPage = totalPage;
		
		// 현재 블록 시작 페이지
		this.blockStart = (currentPage - 1) / pageSize * pageSize + 1;
		
		// 현재 블록 마지막 페이지
		blockEnd = blockStart + pageSize - 1;
		if (blockEnd > totalPage) {
            blockEnd = totalPage;
        }
		
		// 이전 버튼
		if (blockStart <= 1) {
			prevPage = 1;
        } else {
            prevPage = blockStart - 1;
        }
		
		// 다음 버튼
		if (blockEnd >= totalPage) {
			nextPage = totalPage;
		} else {
            nextPage = blockEnd + 1;
        }
		
	}

}
