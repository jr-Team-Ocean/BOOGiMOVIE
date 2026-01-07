package com.bm.project.elasticsearch;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "search-rank-*", createIndex = false) // 스프링에서 생성 X
public class SearchLogDocument {
	@Id
    private String id;

    @Field(name = "keyword", type = FieldType.Keyword)
    private String keyword;
}
