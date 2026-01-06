package com.bm.project.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bm.project.entity.Product;
import com.bm.project.entity.ProductTag;
import com.bm.project.entity.ProductTagConnect;
import com.bm.project.payment.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//오라클에 있는 데이터 -> 엘라스틱 인덱스로 그대로 복사
@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchService {
	private final ProductRepository productRepo;	   // 오라클 DB
	private final ProductSearchRepository searchRepo;  // 엘라스틱
	
	// 검색어 전용 로그
	private final Logger searchLogger = LoggerFactory.getLogger("SEARCH_LOGGER");
	
	@Transactional(readOnly = true)
	public void syncAllData() {
		List<Product> products = productRepo.findAll(); // 모든 Product 데이터 가져오기
		List<ProductDocument> docs = new ArrayList<>();

        for (Product p : products) {
            // 엔티티 -> Document 변환 (태그 분리 로직 포함)
            // (아까 DTO에서 넣던 로직을 여기서 엔티티 기준으로 한 번 수행해줘야 함)
            ProductDocument doc = convertEntityToDoc(p);
            docs.add(doc);
        }
        
        searchRepo.saveAll(docs);
	}
	
	public ProductDocument convertEntityToDoc(Product p) {
		List<String> authors = new ArrayList<>();
        List<String> directors = new ArrayList<>();
        List<String> publishers = new ArrayList<>(); // 출판사/제작사 통합
        
        // 태그 연결 테이블 뒤져서 분류하기
        for (ProductTagConnect connect : p.getProductTagConnects()) {
            ProductTag tag = connect.getProductTag();
            long code = tag.getTagCode().getTagCode(); // 1:작가, 2:감독...
            String name = tag.getTagName(); // 작가/감독명, 출판사/제작사명 등

            switch ((int)code) {
                case 1: authors.add(name); break;
                case 2: directors.add(name); break;
                case 3: 
                case 4: publishers.add(name); break;
            }
        }
        
        return ProductDocument.builder()
                .productNo(p.getProductNo())
                .productTitle(p.getProductTitle())
                .productContent(p.getProductContent())
                .productPrice(p.getProductPrice())
                .productDate(p.getProductDate())
                .imgPath(p.getImgPath())
                .categoryName(p.getCategory().getCategoryName())
                .productType(p.getProductType().getTypeName()) // "도서" or "영화"
                .authors(authors)
                .directors(directors)
                .publisher(publishers)
                .build();
	}

	/** 통합검색
	 * @param query
	 * @return
	 */
	public List<ProductDocument> headerSearch(String query) {
		Pageable pageable = PageRequest.of(0, 3);
		return searchRepo.searchByKeyword(query, pageable);
	}

	// 로그 분석
	public void getSearchLogData(String query) {
		searchLogger.info(query); // 검색어 로그에 추가
		
		log.info("검색어 로그 전송 : {}", query);
		
	}
	
}
