package com.bm.project.document;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Document(indexName = "products") // 인덱스 이름
@Setting(settingPath = "elastic/elastic-setting.json")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductDocument {

    @Id
    private Long productNo; // 상품 번호 (PK)

    @Field(type = FieldType.Text, analyzer = "nori") // 한글 형태소 분석기 적용
    private String productTitle;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String productContent;

    @Field(type = FieldType.Integer)
    private Integer productPrice;

    // 최신순 정렬을 위해 Date 타입 유지
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime productDate;

    // 이미지 경로는 검색할 일 없으니 인덱싱 제외 (저장만)
    @Field(type = FieldType.Keyword, index = false)
    private String imgPath;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Keyword)
    private String productType; // "도서", "영화"


    @Field(type = FieldType.Text, analyzer = "nori")
    private List<String> authors;    // 작가 리스트 (도서)

    @Field(type = FieldType.Text, analyzer = "nori")
    private List<String> directors;  // 감독 리스트 (영화)

    @Field(type = FieldType.Text, analyzer = "nori")
    private List<String> actors;     // 배우 리스트 (영화)

    @Field(type = FieldType.Text, analyzer = "nori")
    private List<String> publisher;  // 출판사 또는 제작사


  
}