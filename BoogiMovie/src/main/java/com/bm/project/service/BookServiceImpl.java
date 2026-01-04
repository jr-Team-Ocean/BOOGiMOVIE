package com.bm.project.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bm.project.dto.BookDto;
import com.bm.project.dto.BookDto.Create;
import com.bm.project.dto.BookDto.Response;
import com.bm.project.dto.BookDto.Update;
import com.bm.project.entity.Book;
import com.bm.project.entity.Category;
import com.bm.project.entity.Likes;
import com.bm.project.entity.Member;
import com.bm.project.entity.Product;
import com.bm.project.entity.ProductTag;
import com.bm.project.entity.ProductType;
import com.bm.project.entity.TagCode;
import com.bm.project.repository.BookRepository;
import com.bm.project.repository.BookRepository2;
import com.bm.project.repository.LikeRepository;
import com.bm.project.repository.TagRepository;
import com.bm.project.enums.CommonEnums;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl  implements BookService {
	
	private final BookRepository bookRepository;
	private final BookRepository2 bookRepository2;
	private final TagRepository tagRepository;
	private final LikeRepository likeRepository;
	
	private final String FILE_PATH = "C:/bmImg/book/";
	private final String WEB_PATH = "/images/book/";
	
	
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
		
		Category category = product.getCategory();
		Category pcategory = category.getPCategoryId();
		
		// 저자 불러오기
		List<String> writers = bookRepository.selectWritersByProductNo(productNo);
		
		// 출판사 불러오기 
		List<String> publishers = bookRepository.selectPublishersByProductNo(productNo);
		
		return Response.toDetailDto(product, book, category, pcategory, writers, publishers);
	}


	// 도서 등록
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Long bookWrite(
			Create bookCreate
			) throws IllegalStateException, IOException {
		
		Product product = bookCreate.toEntity();
		
		Category category =
				bookRepository.getReference(Category.class, bookCreate.getCategoryId());
		
		ProductType productType =
	            bookRepository.getReference(ProductType.class, 1L);
		
		product.setCategory(category);
        product.setProductType(productType);
        
        // 이미지
        MultipartFile image = bookCreate.getBookImage();
        
        if (image != null && !image.isEmpty()) {
        	
        	String originName = image.getOriginalFilename();
        	String reName = UUID.randomUUID().toString() + "_" + originName;
        	
        	File uploadDir = new File(FILE_PATH);
        	if (!uploadDir.exists()) uploadDir.mkdirs();
        	
        	image.transferTo(new File(FILE_PATH + reName));
        	
        	// DB에는 웹 경로만 저장
        	product.setImgPath(WEB_PATH + reName);
        }
        
        // 도서
        Book book = bookCreate.toBookEntity(product);
        bookRepository2.save(book);
        
        
        // 작가, 출판사 자르기
		List<String> writers = splitToList(bookCreate.getWriters());
		List<String> publishers = splitToList(bookCreate.getPublishers());
		
		// 작가 중복검사 + 저장
		TagCode wCode = bookRepository.getTagCodeRef(1L);
		for (String writer : writers) {
			
			ProductTag tag =
					tagRepository.findByTagNameAndTagCode(writer, wCode)
					.orElseGet(() -> tagRepository.save(
					ProductTag.builder()
							  .tagName(writer)
							  .tagCode(wCode)
							  .build()));
			
			bookRepository.saveProductTagConnect(product, tag);
		}
		
		// 출판사 중복검사 + 저장
		TagCode pCode = bookRepository.getTagCodeRef(3L);
		for (String publisher : publishers) {
			
			ProductTag tag = 
					tagRepository.findByTagNameAndTagCode(publisher, pCode)
					.orElseGet(() -> tagRepository.save(
					ProductTag.builder()
			 	     		  .tagName(publisher)
							  .tagCode(pCode)
							  .build()));
			
			bookRepository.saveProductTagConnect(product, tag);
		}
		
		return product.getProductNo();
	}
	
	
	// 한 무자열로 들어온 작가들,출판사들 자르기
	private List<String> splitToList(String n) {
	    if (n == null) return List.of();
	    return Arrays.stream(n.split(","))
	                 .map(String::trim)
	                 .filter(s -> !s.isEmpty())
	                 .distinct()
	                 .toList();
	}




	// 도서 수정
	@Transactional(readOnly = false)
	@Override
	public void bookUpdate(Long productNo, Update bookUpdate) throws IllegalStateException, IOException {
		Book book = bookRepository2.findByProduct_ProductNo(productNo)
	            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 도서 입니다."));
		
		
		Product product = book.getProduct();
		
		
		product.setProductTitle(bookUpdate.getProductTitle());
	    product.setProductContent(bookUpdate.getProductContent());
	    product.setProductPrice(bookUpdate.getProductPrice());
	    
	    
	    if (bookUpdate.getProductDate() != null) {
	        product.setProductDate(bookUpdate.getProductDate().atStartOfDay());
	    }
		
	    Category category =
	            bookRepository.getReference(Category.class, bookUpdate.getCategoryId());
        
	    product.setCategory(category);
		
	    
	    MultipartFile image = bookUpdate.getBookImage();

	    if (image != null && !image.isEmpty()) {

	        String originName = image.getOriginalFilename();
	        String reName = UUID.randomUUID() + "_" + originName;

	        File dir = new File(FILE_PATH);
	        if (!dir.exists()) dir.mkdirs();

	        image.transferTo(new File(FILE_PATH + reName));

	        product.setImgPath(WEB_PATH + reName);
	    }

	    book.setIsbn(bookUpdate.getIsbn());
	    book.setBookCount(bookUpdate.getBookCount());
	    
	    bookRepository.deleteConnect(productNo);
	    
	    
	    // 작가, 출판사 자르기
 		List<String> writers = splitToList(bookUpdate.getWriters());
 		List<String> publishers = splitToList(bookUpdate.getPublishers());
 		
 		// 작가 중복검사 + 저장
 		TagCode wCode = bookRepository.getTagCodeRef(1L);
 		for (String writer : writers) {
 			
 			ProductTag tag =
 					tagRepository.findByTagNameAndTagCode(writer, wCode)
 					.orElseGet(() -> tagRepository.save(
 					ProductTag.builder()
 							  .tagName(writer)
 							  .tagCode(wCode)
 							  .build()));
 			
 			bookRepository.saveProductTagConnect(product, tag);
 		}
 		
 		// 출판사 중복검사 + 저장
 		TagCode pCode = bookRepository.getTagCodeRef(3L);
 		for (String publisher : publishers) {
 			
 			ProductTag tag = 
 					tagRepository.findByTagNameAndTagCode(publisher, pCode)
 					.orElseGet(() -> tagRepository.save(
 					ProductTag.builder()
 			 	     		  .tagName(publisher)
 							  .tagCode(pCode)
 							  .build()));
 			
 			bookRepository.saveProductTagConnect(product, tag);
 		}
	    
	}




	@Transactional(readOnly = false)
	@Override
	public void bookDelete(Long productNo) {
	    Book book = bookRepository2.findById(productNo)
	            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 도서입니다."));

	    Product product = book.getProduct();

	    product.setProductDelFl(CommonEnums.ProductDelFl.Y);
		
	}





	// 기존 좋아요 여부
	@Override
	public int bookLikeCheck(Long productNo, Long memberNo) {
		boolean exists =
	            likeRepository.existsByProduct_ProductNoAndMember_MemberNo(productNo, memberNo);
		
		return exists ? 1 : 0;
	}


	// 좋아요 처리
	@Override
	public int bookLike(Map<String, Long> paramMap) {
		
		Long memberNo  = paramMap.get("memberNo");
		Long productNo = paramMap.get("productNo");
		Long check     = paramMap.get("check");
		
		
		if (check == 0L) {
			
			// 중복 방지
			boolean exists =
			        likeRepository.existsByProduct_ProductNoAndMember_MemberNo(productNo, memberNo);
			
			if (!exists) {
				// 추가
		        bookRepository.insertLike(productNo, memberNo);
		    }
		} else {	
			// 삭제 처리
			likeRepository.deleteByProduct_ProductNoAndMember_MemberNo(productNo, memberNo);
			likeRepository.flush();
		}
		
		return likeRepository.countByProduct_ProductNo(productNo);
	}




	// 좋아요 개수 확인
	@Override
	public int bookLikeCount(Long productNo) {
		return likeRepository.countByProduct_ProductNo(productNo);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
