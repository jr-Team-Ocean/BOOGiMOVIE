package com.bm.project.elasticsearch;

import java.io.IOException;
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

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//오라클에 있는 데이터 -> 엘라스틱 인덱스로 그대로 복사
@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchService {
	private final ProductRepository productRepo; // 오라클 DB
	private final ProductSearchRepository searchRepo; // 엘라스틱
	private final ElasticsearchClient esClient;

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

			switch ((int) code) {
			case 1:
				authors.add(name);
				break;
			case 2:
				directors.add(name);
				break;
			case 3:
			case 4:
				publishers.add(name);
				break;
			}
		}

		return ProductDocument.builder().productNo(p.getProductNo()).productTitle(p.getProductTitle())
				.productContent(p.getProductContent()).productPrice(p.getProductPrice()).productDate(p.getProductDate())
				.imgPath(p.getImgPath()).categoryName(p.getCategory().getCategoryName())
				.productType(p.getProductType().getTypeName()) // "도서" or "영화"
				.authors(authors).directors(directors).publisher(publishers).build();
	}

	// 통합 검색
	public List<ProductDocument> headerSearch(String query) {
		Pageable pageable = PageRequest.of(0, 3);
		return searchRepo.searchByKeyword(query, pageable);
	}

	// 로그 분석
	public void getSearchLogData(String query) {
		searchLogger.info(query); // 검색어 로그에 추가

		log.info("검색어 로그 전송 : {}", query);

	}

	// 인기 검색어
	public List<String> getTopKeywords() {
		try {
			// 1. [조립] 검색 요청 만들기
			SearchRequest searchRequest = SearchRequest.of(r -> r.index("search-rank-*") // 인덱스 패턴
					.size(0) // 문서 내용은 필요 없음 (통계만)
					.aggregations("top_words", a -> a // 집계 이름: "top_words"
							.terms(t -> t // terms 집계 사용
									.field("keyword.keyword") // 기준 필드
									.size(10) // 상위 10개
							)));

			// 2. [실행] 요청 날리기
			SearchResponse<Void> response = esClient.search(searchRequest, Void.class);

			// 3. [파싱] 결과 꺼내기
			List<String> result = new ArrayList<>();

			if (response.aggregations() != null) {
				// "top_words" 꺼내서 바로 StringTerms(sterms)로 변환
				StringTermsAggregate termsAgg = response.aggregations().get("top_words").sterms();

				// 버킷 리스트 순회
				List<StringTermsBucket> buckets = termsAgg.buckets().array();
				for (StringTermsBucket bucket : buckets) {
					result.add(bucket.key().stringValue()); // 키 값("자바") 추출
				}
			}

			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
			
		} catch (ElasticsearchException e) {
			// 인기검색어 인덱스가 존재하지 않을 경우에도 그냥 빈 리스트 반환
			return new ArrayList<>();
		}
	}

}
