package com.bm.project.service;

import com.bm.project.enums.CommonEnums;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bm.project.dto.UbookDto;
import com.bm.project.dto.UbookDto.Create;
import com.bm.project.dto.UbookDto.Response;
import com.bm.project.elasticsearch.ProductDocument;
import com.bm.project.elasticsearch.ProductSearchRepository;
import com.bm.project.entity.Book;
import com.bm.project.entity.Category;
import com.bm.project.entity.Product;
import com.bm.project.entity.ProductTag;
import com.bm.project.entity.ProductType;
import com.bm.project.entity.TagCode;
import com.bm.project.entity.Ubook;
import com.bm.project.repository.CategoryRepository;
import com.bm.project.repository.ProductTypeRepository;
import com.bm.project.repository.TagRepository;
import com.bm.project.repository.UbookRepository;
import com.bm.project.repository.UbookRepository2;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UBookServiceImpl implements UbookService{
	
	private final UbookRepository ubookRepository;
	private final UbookRepository2 ubookRepositories;
	private final TagRepository tagRepository;
	private final CategoryRepository categoryRepository;
	private final ProductTypeRepository productTypeRepository;
	private final ProductSearchRepository searchRepository;
	
	private final String UPLOAD_PATH = "C:\\bmImg\\";
	private final String WEB_PATH = "\\images\\ubook\\";

	
	// 중고도서 목록 조회
	@Override
	public Page<UbookDto.Response> selectbookList(Map<String, Object> paramMap, Pageable pageable) {
	
		
		Page<Product> page = ubookRepository.selectbookList(paramMap, pageable);
		

		
		List<Long> productNos = page.getContent()
									.stream()
									.map(Product::getProductNo)
									.toList();
		
		System.out.println("productNos = " + productNos);
		
		List<Object[]> ubookStatusList = ubookRepository.selectUbookStateList(productNos);
		
		System.out.println("ubookStatusList : " + ubookStatusList);
		
		
		Map<Long, String> ubookStatusMap = new HashMap<>();
		
		for (Object[] row : ubookStatusList) {
		    Long productNo = (Long) row[0];
		    String ubookStatus = (String) row[1];

		    System.out.println("productNo = " + productNo
		                     + ", ubookStatus = " + ubookStatus);
		    
		    ubookStatusMap.put(productNo, ubookStatus);
		}

		
		List<UbookDto.Response> listUbookDto =
		        page.getContent()
		            .stream()
		            .map(p -> {
		                UbookDto.Response dto = UbookDto.Response.toUListDto(p);
		                dto.setUbookStatus(
		                    ubookStatusMap.get(p.getProductNo())
		                    
		                );
		                System.out.println(ubookStatusMap.get(p.getProductNo()));
		                return dto;
		            })
		            .toList();

		System.out.println(page.getContent());
		
		
		return new PageImpl<>(listUbookDto, pageable, page.getTotalElements());
	
	
	}


	// 중고도서 상세 조회
	@Override
	public Response selectUbookDetail(Long productNo) {
		
		
		// 상품번호로 도서 상세 정보 조회
		Ubook ubook = ubookRepository.selectUbookDetailByProductNo(productNo)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
		
		// 도서와 연결된 상품 정보 가져오기
		Product product = ubook.getProduct();
		
		Category category = product.getCategory();
		Category pcategory = category.getPCategoryId();
		
		// 저자 불러오기
		List<String> writers = ubookRepository.selectWritersByProductNo(productNo);
		
		// 출판사 불러오기 
		List<String> publishers = ubookRepository.selectPublishersByProductNo(productNo);
		
		return Response.toUbookDetailDto(product, ubook, category, pcategory, writers, publishers);

		
	}


	// 중고도서 상품 삭제
	@Transactional(readOnly=false)
	@Override
	public void deleteProduct(Long productNo) {
		
		Ubook ubook = ubookRepositories.findById(productNo)
				.orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));
		
		System.out.println("ubook : " + ubook);
		
		Product product = ubook.getProduct();
		
		System.out.println("product : " + product);
		
		product.setProductDelFl(CommonEnums.ProductDelFl.Y);
		
		
		
		
		
	}

	
	// 중고도서 상품 등록
	@Transactional(readOnly=false)
	@Override
	public Long insertUbook(Create createUbook) throws IllegalStateException, IOException {
		
		
		Product product = createUbook.toProductEntity();
		
		Category category =
				ubookRepository.getReference(Category.class, createUbook.getCategoryId());
		
		ProductType productType =
	            ubookRepository.getReference(ProductType.class, 1L);
		
		product.setCategory(category);
        product.setProductType(productType);
        
        // 이미지
        MultipartFile image = createUbook.getImage();
		
		
		// 이미지
		String changeName = null;
		String originName = null;
		
		
		// 이미지 파일이 null인지, 이미지 객체는 있는데 파일이 비어있지 않은지 검사
		if(createUbook.getImage() != null && !createUbook.getImage().isEmpty()) {
			
			
			originName = createUbook.getImage().getOriginalFilename();
			
			System.out.println(createUbook.getImage().getOriginalFilename());
			
			changeName = UUID.randomUUID().toString() + "_" + originName;
			
			File uploadDir = new File(UPLOAD_PATH);
			
			
			if(!uploadDir.exists()) uploadDir.mkdirs();
			
			// 실제 파일 저장(물리적)
			createUbook.getImage().transferTo(new File(UPLOAD_PATH + changeName));
			
			
			product.setImgPath(WEB_PATH + changeName);

		}
		
		
		Ubook ubook = createUbook.toUbookEntity();
		ubookRepositories.save(ubook);
		
		
		System.out.println("productNo2222 : " + product.getProductNo());
		
		
		
		
		// 작가, 출판사 자르기
		List<String> writers = splitToList(createUbook.getWriters());
		List<String> publishers = splitToList(createUbook.getPublishers());
		
		System.out.println("writers : " + writers);
		
		// 작가 중복검사 + 저장
		TagCode wCode = ubookRepository.getTagCodeRef(1L);
		for (String writer : writers) {
		    connectTag(product, wCode, writer);
		}
		
		// 출판사 중복검사 + 저장
		TagCode pCode = ubookRepository.getTagCodeRef(3L);
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
							   .productType("중고도서")
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
	    ubookRepository.saveProductTagConnect(product, tag);
	}
	
	
	// 한 문자열로 들어온 작가들,출판사들 자르기
	private List<String> splitToList(String n) {
	    if (n == null) return List.of();
	    return Arrays.stream(n.split(","))
	                 .map(String::trim)
	                 .filter(s -> !s.isEmpty())
	                 .distinct()
	                 .toList();
	}
	

}




