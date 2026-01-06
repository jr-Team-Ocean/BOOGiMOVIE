package com.bm.project.elasticsearch;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {

	// 통합 검색
	@Query("{" +
            "  \"multi_match\": {" +
            "    \"query\": \"?0\"," +
            "    \"fields\": [" +
            "       \"productTitle\"," +
            "       \"authors\"," +
            "       \"directors\"," +
            "       \"actors\"," +
            "       \"publisher\"" +
            "    ]" +
            "  }" +
            "}")
    List<ProductDocument> searchByKeyword(String keyword, Pageable pageable);

}
