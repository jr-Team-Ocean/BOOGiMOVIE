package com.bm.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bm.project.dto.BookDto;
import com.bm.project.dto.BookDto.Response;
import com.bm.project.entity.Book;
import com.bm.project.entity.Product;
import com.bm.project.repository.BookRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl  implements BookService {
	
	private final BookRepository bookRepository;
	
	
	// 도서 목록 조회
	@Override
	public Page<BookDto.Response> selectBookList(Map<String, Object> paramMap, Pageable pageable) {
		
		
		Page<Product> page = bookRepository.selectBookList(paramMap,pageable);
		
		// 상품번호
		List<Long> productNos = page.getContent()
				                    .stream()
				                    .map(Product::getProductNo)
				                    .toList();
		// 저자
		List<Object[]> rows = bookRepository.selectWritersByProductNos(productNos);
		
		Map<Long, String> writersMap = new HashMap<>();
		
		for (Object[] r : rows) {
            Long productNo = (Long) r[0];
            String writer = (String) r[1];
            // 저자 1명만
            writersMap.putIfAbsent(productNo, writer);
        }
		
		Map<Long, List<String>> writersList =
				// entrySet(): Map 안에 K:V 를 Set 으로 꺼냄
		        writersMap.entrySet().stream()
                          .collect(Collectors.toMap(
                        		  Map.Entry::getKey, e -> List.of(e.getValue())
		                ));
		
		List<BookDto.Response> dtoList =
		        page.getContent()
		            .stream()
		            .map(p -> BookDto.Response.toListDto(
		                    p,
		                    writersList.getOrDefault(p.getProductNo(), List.of())
		            ))
		            .toList();
										     
		
		return new PageImpl<>(dtoList, pageable, page.getTotalElements());
	}

	
	
	
	
	// 도서 검색 조회
	@Override
	public Page<Response> searchBookList(Map<String, Object> paramMap, Pageable pageable) {
		
		
		Page<Product> page = bookRepository.searchBookList(paramMap, pageable);
		
		List<Long> productNos = page.getContent()
                 					.stream()
                 					.map(Product::getProductNo)
             						.toList();
		
		List<Object[]> rows = bookRepository.selectWritersByProductNos(productNos);
		
		Map<Long, String> writersMap = new HashMap<>();
		
		for (Object[] r : rows) {
	        Long productNo = (Long) r[0];
	        String writer = (String) r[1];
	        writersMap.putIfAbsent(productNo, writer); // 저자 1명만
	    }
		
		
		Map<Long, List<String>> writersList =
	            writersMap.entrySet().stream()
	                      .collect(Collectors.toMap(
	                              Map.Entry::getKey,
	                              e -> List.of(e.getValue())));
		
		List<BookDto.Response> dtoList = page.getContent()
	                						 .stream()
	                						 .map(p -> BookDto.Response.toListDto(
	                							  p, writersList.getOrDefault(p.getProductNo(), List.of())
	                						 ))
	                						 .toList();
		
		return new PageImpl<>(dtoList, pageable, page.getTotalElements());
	}





	// 도서 상세 조회
	@Override
	public Response selectBookDetail(Long productNo) {
		
		// 상품번호로 도서 상세 정보 조회
		Book book = bookRepository.selectBookDetailByProductNo(productNo)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
		
		// 도서와 연결된 상품 정보 가져오기
		Product product = book.getProduct();
		
		// 저자 불러오기
		List<String> writers = bookRepository.selectWritersByProductNo(productNo);
		
		// 출판사 불러오기 
		List<String> publishers = bookRepository.selectPublishersByProductNo(productNo);
		
		return Response.toDetailDto(product, book, writers, publishers);
	}
	
}
