package com.bm.project.elasticsearch;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {

	@Query("{" +
            "  \"bool\": {" +
            "    \"should\": [" +
            "      { \"wildcard\": { \"productTitle\": \"*?0*\" } }," +
            "      { \"wildcard\": { \"productContent\": \"*?0*\" } }," +
            "      { \"wildcard\": { \"authors\": \"*?0*\" } }," +
            "      { \"wildcard\": { \"directors\": \"*?0*\" } }," +
            "      { \"wildcard\": { \"actors\": \"*?0*\" } }," +
            "      { \"wildcard\": { \"publisher\": \"*?0*\" } }" +
            "    ]" +
            "  }" +
            "}")
    List<ProductDocument> searchByKeyword(String keyword);
}
