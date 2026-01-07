package com.bm.project.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.bm.project.dto.BookDto;
import com.bm.project.dto.BookDto.Create;
import com.bm.project.dto.BookDto.Response;
import com.bm.project.dto.BookDto.Update;
import com.bm.project.elasticsearch.ProductDocument;
import com.bm.project.elasticsearch.ProductSearchRepository;
import com.bm.project.entity.Book;
import com.bm.project.entity.Category;
import com.bm.project.entity.Product;
import com.bm.project.entity.ProductTag;
import com.bm.project.entity.ProductType;
import com.bm.project.entity.Review;
import com.bm.project.entity.TagCode;
import com.bm.project.enums.CommonEnums;
import com.bm.project.repository.BookRepository;
import com.bm.project.repository.BookRepository2;
import com.bm.project.repository.LikeRepository;
import com.bm.project.repository.ReviewRepository;
import com.bm.project.repository.TagRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

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
	private final ReviewRepository reviewRepository;
	private final ProductSearchRepository searchRepository;
	
//	private final String FILE_PATH = "C:/bmImg/book/";
//	private final String WEB_PATH = "/images/book/";
	
	@Value("${my.book.location}")
	private String FILE_PATH;
	
	@Value("${my.book.webpath}")
	private String WEB_PATH;
	
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
		    connectTag(product, wCode, writer);
		}
		
		// 출판사 중복검사 + 저장
		TagCode pCode = bookRepository.getTagCodeRef(3L);
		for (String publisher : publishers) {
		    connectTag(product, pCode, publisher);
		}
		
		// 엘라스틱 저장로직
		ProductDocument doc = 
				ProductDocument.builder()
							   .productNo(product.getProductNo())
							   .productTitle(product.getProductTitle())
							   .productPrice(product.getProductPrice())
							   .productDate(product.getProductDate())
							   .imgPath(product.getImgPath())
							   .categoryName(product.getCategory().getCategoryName())
							   .productType("도서")
							   .authors(writers)
							   .publisher(publishers)
							   .build();
		searchRepository.save(doc);
		
		
		return product.getProductNo();
	}
	
	
	private void connectTag(Product product, TagCode tagCode, String tagName) {
	    if (!org.springframework.util.StringUtils.hasText(tagName)) return;

	    // 태그 조회 -> 없으면 생성 (Movie 방식과 동일하게 save 사용)
	    ProductTag tag = tagRepository.findByTagNameAndTagCode(tagName, tagCode)
	            .orElseGet(() -> tagRepository.save(
	                    ProductTag.builder()
	                            .tagName(tagName)
	                            .tagCode(tagCode)
	                            .build()
	            ));
	    
	    // 연결 테이블 저장
	    bookRepository.saveProductTagConnect(product, tag);
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
            connectTag(product, wCode, writer);
        }
 		
 		// 출판사 중복검사 + 저장
 		TagCode pCode = bookRepository.getTagCodeRef(3L);
 		for (String publisher : publishers) {
		    connectTag(product, pCode, publisher);
		}
 		
 		ProductDocument doc = 
 				ProductDocument.builder()
				               .productNo(product.getProductNo())
				               .productTitle(product.getProductTitle())
				               .productPrice(product.getProductPrice())
				               .productDate(product.getProductDate())
				               .imgPath(product.getImgPath())
				               .categoryName(product.getCategory().getCategoryName())
				               .productType("도서")
				               .authors(writers)
				               .publisher(publishers)
				               .build();
        
        searchRepository.save(doc);
	    
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
			// 언더바 없어도 되는데 가독성 때문에 씀 
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




	// 후기 목록 조회
	@Override
	public List<Review> selectReviewList(Long productNo) {
		return bookRepository.selectReviewList(productNo);
	}




	// 후기 등록
	@Transactional(readOnly = false)
	@Override
	public int writeReview(Long productNo, Long memberNo, Integer reviewScore, String reviewContent) {
		
		return bookRepository.insertReview(productNo, memberNo, reviewScore, reviewContent);
	}
	
	// 후기 수정
	@Transactional(readOnly = false)
	@Override
	public int updateReview(Long reviewNo, Long memberNo, String reviewContent) {
		
		Review review = reviewRepository.findByReviewNoAndMemberNo(reviewNo, memberNo)
		        						.orElse(null);

	    if (review == null) return 0;

	    review.setReviewContent(reviewContent);
		
		return 1;
	}
	
	// 후기 삭제
	@Transactional(readOnly = false)
	@Override
	public int deleteReview(Long reviewNo, Long memberNo) {
		reviewRepository.deleteByReviewNoAndMemberNo(reviewNo, memberNo);
		return 1;
	}





	// 평점
	@Override
	public double getReviewAverage(Long productNo) {
		
		Double avg = bookRepository.selectReviewAverage(productNo);
		
		if (avg == null) {
	        return 0.0;
	    }

	    return avg;
	}

	@Value("${aladin.api.base-url}")
	private String aladinBaseUrl;

	@Value("${aladin.api.ttb-key}")
	private String aladinTtbKey;



	// api 임시
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean bookWriteByApiIsbn(String isbn) {
		
		// 중복 여부
		if (bookRepository2.existsByIsbn(isbn)) {
	        return false; // 중복
	    }
	    
		UriComponentsBuilder uriBuilder =
	            UriComponentsBuilder.fromUriString(aladinBaseUrl)
	                .queryParam("ttbkey", aladinTtbKey)
	                .queryParam("itemIdType", "ISBN")
	                .queryParam("ItemId", isbn)
	                .queryParam("Cover", "Big")
	                .queryParam("output", "xml")
	                .queryParam("Version", "20131101");
		
		String uri = uriBuilder.build().toUriString();
		
		HttpHeaders headers = new HttpHeaders();
	    headers.set("Accept", "application/xml");
		
	    HttpEntity<String> entity = new HttpEntity<>(headers);
	    
	    RestTemplate restTemplate = new RestTemplate();
	    restTemplate.getMessageConverters()
	                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
	    
	    ResponseEntity<String> resp =
	            restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
	    
	    String xml = resp.getBody();
	    
	    
	    try {
	    	
	    	XmlMapper xmlMapper = new XmlMapper();
		    JsonNode root = xmlMapper.readTree(xml);
	
		    // <item> 하나
		    JsonNode item = root.path("item");
	
		    if (item.isMissingNode()) {
		        // 조회 실패 or ISBN 잘못됨
		        return false;
		    }
		    
		    // 값 추출
		    String title        = item.path("title").asText();
		    String description  = item.path("description").asText();
		    String isbn13       = item.path("isbn13").asText();
		    String cover        = item.path("cover").asText();
		    int priceSales      = item.path("priceSales").asInt();
		    String pubDateStr   = item.path("pubDate").asText(); // yyyy-MM-dd
		    String categoryName = item.path("categoryName").asText();
		    String authorA      = item.path("author").asText();
		    String publisherA   = item.path("publisher").asText();
		    
		    // 시간
		    LocalDateTime productDate = LocalDate.parse(pubDateStr).atStartOfDay();
		    
		    Long categoryId = resolveCategoryId(categoryName);
		    
		    
		    
		    
		    
		    Product product = Product.builder()
					                 .productTitle(title)
					                 .productContent(description)
					                 .productPrice(priceSales)
					                 .productDate(productDate)
					                 .imgPath(cover)
					                 .build();
		    
		    // 카테고리
		    Category category = bookRepository.getReference(Category.class, categoryId);
		    product.setCategory(category);
		    // 도서타입으로 
		    ProductType productType = bookRepository.getReference(ProductType.class, 1L);
		    product.setProductType(productType);
		    
		    
		    
		    
		    // 도서부분 값 저장
		    Book book = Book.builder()
			                .isbn(isbn13)
			                .bookCount(100)
			                .product(product)
			                .build();
		    
		   bookRepository2.save(book);
		   
		   // 작가, 출판사 자르기
		   List<String> writers = splitToList(authorA);
		   List<String> publishers = splitToList(publisherA);
	 		
		   // 작가 중복검사 + 저장
		   TagCode wCode = bookRepository.getTagCodeRef(1L);
		   for (String writer : writers) {
			   connectTag(product, wCode, writer);
		   }
 		
		   // 출판사 중복검사 + 저장
		   TagCode pCode = bookRepository.getTagCodeRef(3L);
		   for (String publisher : publishers) {
			   connectTag(product, pCode, publisher);
		   }
	   
		   ProductDocument doc =
				    ProductDocument.builder()
				        .productNo(product.getProductNo())
				        .productTitle(product.getProductTitle())
				        .productPrice(product.getProductPrice())
				        .productDate(product.getProductDate())
				        .imgPath(product.getImgPath())
				        .categoryName(product.getCategory().getCategoryName())
				        .productType("도서")
				        .authors(writers)
				        .publisher(publishers)
				        .build();

		   searchRepository.save(doc);
	 		
	 		
		    
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	    
	    
	    
	    return true;
	    
	}




	
	
	
	// api로 끌어로 때 카테고리 검사?
	private Long resolveCategoryId(String categoryName) {
		
		if (categoryName == null || categoryName.isBlank()) {
	        return 99L;
	    }

	    String[] parts = categoryName.split(">");
	    if (parts.length < 2) return 99L;

	    String second = parts[1].trim(); // 핵심 기준
	    String full = categoryName.replace(">", " ").toLowerCase();

	    // ===== 소설/희곡 (부모 11) =====
	    if (second.contains("소설") || second.contains("시") || second.contains("희곡")) {

	        if (full.contains("라이트노벨")) return 12L;
	        if (full.contains("판타지"))     return 13L;
	        if (full.contains("sf") || full.contains("과학소설")) return 14L;
	        if (full.contains("추리"))       return 15L;
	        if (full.contains("로맨스"))     return 16L;
	        if (full.contains("무협"))       return 17L;
	        if (full.contains("희곡"))       return 18L;

	        return 19L; // 소설/희곡 기타
	    }

	    // ===== 나머지는 2번째 토큰 기준 =====
	    if (second.contains("에세이")) return 4L; // 여행/에세이 → 여행 카테고리
	    if (second.contains("어린이") || second.contains("유아")) return 3L;
	    if (second.contains("만화")) return 8L;
	    if (second.contains("컴퓨터") || second.contains("모바일")) return 9L;
	    if (second.contains("외국어")) return 7L;
	    if (second.contains("과학") || second.contains("사회과학")) return 6L;
	    if (second.contains("역사") || second.contains("문화")
	        || second.contains("예술") || second.contains("종교")) return 5L;
	    if (second.contains("경제") || second.contains("경영")) return 99L; // 기타
	    if (second.contains("여행")) return 4L;

	    // 외국도서
	    if (parts[0].contains("외국도서")) return 10L;

	    return 99L;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
