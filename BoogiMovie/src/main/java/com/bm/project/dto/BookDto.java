package com.bm.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class BookDto {
	
	@Getter
	@Setter
	public static class BookCreate {
		
		private String productTitle;
        private String productContent;
        private Integer productPrice;
        private LocalDateTime productDate;
        private String imgPath;
        private Long categoryId;
        
        private Integer bookCount;
        private String isbn;
        
        private List<Long> authorTagNos;
        private List<Long> publisherTagNos;
	}
	
}
