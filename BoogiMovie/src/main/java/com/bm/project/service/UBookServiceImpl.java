package com.bm.project.service;

import com.bm.project.enums.CommonEnums;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bm.project.dto.UbookDto;
import com.bm.project.dto.UbookDto.Create;
import com.bm.project.dto.UbookDto.Response;
import com.bm.project.entity.Book;
import com.bm.project.entity.Category;
import com.bm.project.entity.Product;
import com.bm.project.entity.Ubook;
import com.bm.project.repository.UbookRepository;
import com.bm.project.repository.UbookRepository2;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UBookServiceImpl implements UbookService{
	
	private final UbookRepository ubookRepository;
	private final UbookRepository2 ubookRepositories;

	
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
	@Override
	public Long insertUbook(Create createUbook) {
		
		
		Product product = createUbook.toProductEntity();
		
		System.out.println(product);
		
		
		
		
		return null;
	}
	

}
