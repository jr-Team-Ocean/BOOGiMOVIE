package com.bm.project.entity;

import com.bm.project.enums.CommonEnums.MovieRating;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MOVIE")
@Getter
@Builder
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class Movie {

	@Id
	@Column(name = "PRODUCT_NO")
	private Long productNo;
	
	@MapsId // FK 이면서 기본키(PK)로 사용
	@OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "PRODUCT_NO")
	private Product product;
	
	@Column(name = "MOVIE_TIME")
	private Integer movieTime;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "FILM_RATING")
	private MovieRating filmRating;
	
}
