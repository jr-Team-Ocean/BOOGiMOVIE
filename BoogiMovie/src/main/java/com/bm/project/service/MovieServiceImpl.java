package com.bm.project.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bm.project.dto.MovieDto;
import com.bm.project.dto.MovieDto.Create;
import com.bm.project.dto.MovieDto.Response;
import com.bm.project.dto.MovieDto.Update;
import com.bm.project.dto.PageDto;
import com.bm.project.entity.Category;
import com.bm.project.entity.Movie;
import com.bm.project.entity.Product;
import com.bm.project.entity.ProductTag;
import com.bm.project.entity.ProductType;
import com.bm.project.entity.TagCode;
import com.bm.project.enums.CommonEnums;
import com.bm.project.repository.CategoryRepository;
import com.bm.project.repository.MovieRepository;
import com.bm.project.repository.ProductTypeRepository;
import com.bm.project.repository.TagRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieServiceImpl implements MovieService{
	
	private final MovieRepository movieRepository;
	private final CategoryRepository categoryRepository;
	private final ProductTypeRepository productTypeRepository;
	private final TagRepository tagRepository;
	
	private final String FILE_PATH = "C:\\bmImg\\movie\\";
	private final String WEB_PATH = "/images/movie/";

	// 영화 목록 조회
	@Override
	public PageDto<MovieDto.Response> selectMovieList(Map<String, Object> paramMap, Pageable pageable) {
		
		Page<Movie> page = movieRepository.selectMovieList(paramMap, pageable);
		
		// Page<Movie> -> Page<MovieDto.Response>
        Page<MovieDto.Response> dtoPage = page.map(MovieDto.Response::toListDto);
		
        return new PageDto<>(dtoPage);
	}

	// 검색 + 영화 목록
	@Override
	public PageDto<MovieDto.Response> searchMovieList(Map<String, Object> paramMap, Pageable pageable) {
		
		 Page<Movie> page = movieRepository.searchMovieList(paramMap, pageable);

	        Page<MovieDto.Response> dtoPage = page.map(MovieDto.Response::toListDto);

	        return new PageDto<>(dtoPage);
	}

	// 영화 상세조회
	@Override
	public Response getMovieDetail(Long productNo) {
		
		// 해당 영화 정보가 존재하는지 조회
		Movie movie = movieRepository.findById(productNo)
						.orElseThrow(() -> new EntityNotFoundException("해당 상품이 존재하지 않습니다."));
		
		return MovieDto.Response.toDto(movie);
	}

	// 영화 등록
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Long createMovie(Create movieCreate) throws IllegalStateException, IOException {
		
		Product product = movieCreate.toProductEntity();
		
		Category category = categoryRepository.getReferenceById(movieCreate.getCategoryId());
		
		ProductType productType = productTypeRepository.getReferenceById(2L);
		
		product.setCategory(category);
		product.setProductType(productType);
        
		// 이미지 저장
		MultipartFile img = movieCreate.getMovieImg();
		if(img != null && !img.isEmpty()) {
			
			String originName = img.getOriginalFilename();
			String reName = UUID.randomUUID().toString() + "_" + originName;
			
			File uploadDir = new File(FILE_PATH);
			if(!uploadDir.exists()) uploadDir.mkdirs();
			img.transferTo(new File(FILE_PATH + reName));
			
			// DB에 웹 경로만 저장
			product.setImgPath(WEB_PATH + reName);
		}
		
		// 영화 저장
		Movie movie = movieCreate.toEntity(product);
		movieRepository.save(movie);
		
		// 감독, 배우, 제작사 자르기
		// (1) 콤마 문자열 자르기
        List<String> directors = splitComma(movieCreate.getDirectors());
        List<String> actors    = splitComma(movieCreate.getActors());
        List<String> companies = splitComma(movieCreate.getCompanies());

        // (2) TagCode 검증(없으면 에러)
        TagCode directorCode = movieRepository.getTagCodeRef(2L);
        TagCode actorCode = movieRepository.getTagCodeRef(5L);
        TagCode companyCode = movieRepository.getTagCodeRef(4L);
        TagCode nationCode = movieRepository.getTagCodeRef(6L);

	
        // (3) 연결
        for (String d : directors) {
            connectTag(product, directorCode, d);
        }
        for (String a : actors) {
            connectTag(product, actorCode, a);
        }
        for (String c : companies) {
            connectTag(product, companyCode, c);
        }
        if (StringUtils.hasText(movieCreate.getNation())) {
            connectTag(product, nationCode, movieCreate.getNation());
        }

        return product.getProductNo();
	}
	

	// 영화 수정
	@Transactional(readOnly = false)
	@Override
	public void updateMovie(Long productNo, Update movieUpdate) throws IllegalStateException, IOException {
		// 해당 게시글 존재하는 조회
		Movie movie = movieRepository.findByProduct_ProductNo(productNo)
						.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 영화 입니다."));
		
		Product product = movie.getProduct();
		
		// product 수정
		product.setProductTitle(movieUpdate.getProductTitle());
		product.setProductContent(movieUpdate.getProductContent());
		product.setProductPrice(movieUpdate.getProductPrice());
		
		if(movieUpdate.getProductDate() != null) {
			product.setProductDate(movieUpdate.getProductDate().atStartOfDay());
		}
		
		// 카테고리 수정
		Category category = categoryRepository.getReferenceById(movieUpdate.getCategoryId());
		product.setCategory(category);
		
		// 이미지 수정
		MultipartFile image = movieUpdate.getMovieImg();
		
		if(image != null && !image.isEmpty()) {
			
			String originName = image.getOriginalFilename();
			String reName = UUID.randomUUID().toString() + "_" + originName;
			
			File updateDir = new File(FILE_PATH);
			if(!updateDir.exists()) updateDir.mkdirs();
			image.transferTo(new File(FILE_PATH + reName));
			
			// DB에 웹 경로만 저장
			product.setImgPath(WEB_PATH + reName);
		}
		
		// movie 수정
		movie.setFilmRating(movieUpdate.getFilmRating());
		movie.setMovieTime(movieUpdate.getMovieTime());
		
		System.out.println("entity filmRating = " + movie.getFilmRating());
		
		if(movieUpdate.getActors() != null && !movieUpdate.getActors().isEmpty()) {
			// 기존 Tag 연결 끊기
			movie.getProduct().getProductTagConnects().clear();
			
			List<String> directors = splitComma(movieUpdate.getDirectors());
	        List<String> actors    = splitComma(movieUpdate.getActors());
	        List<String> companies = splitComma(movieUpdate.getCompanies());

	        // 태그코드 검증
	        TagCode directorCode = movieRepository.getTagCodeRef(2L);
	        TagCode actorCode = movieRepository.getTagCodeRef(5L);
	        TagCode companyCode = movieRepository.getTagCodeRef(4L);
	        TagCode nationCode = movieRepository.getTagCodeRef(6L);
			
			// 없으면 생성 -> 연결
			for (String d : directors) {
	            connectTag(product, directorCode, d);
	        }
	        for (String a : actors) {
	            connectTag(product, actorCode, a);
	        }
	        for (String c : companies) {
	            connectTag(product, companyCode, c);
	        }
	        if (StringUtils.hasText(movieUpdate.getNation())) {
	            connectTag(product, nationCode, movieUpdate.getNation());
	        }

		}
		
	}
	
	
	// 태그 연결 + 저장 함수
	private void connectTag(Product product, TagCode tagCode, String tagName) {
	    if (!StringUtils.hasText(tagName)) return;

	    // 태그 조회 -> 없으면 생성
	    ProductTag tag = tagRepository.findByTagNameAndTagCode(tagName, tagCode)
	            .orElseGet(() -> tagRepository.save(
	                    ProductTag.builder()
	                            .tagName(tagName)
	                            .tagCode(tagCode)
	                            .build()
	            ));
	    
	    // 연결 저장(중복이면 내부에서 스킵하도록 movieRepositoryImpl에서 처리)
	    movieRepository.saveProductTagConnect(product, tag);
	}

	
	// 감독, 배우, 제작사들 문자열 자르기
	private List<String> splitComma(String raw) {
	    if (!StringUtils.hasText(raw)) return List.of();
	    return Arrays.stream(raw.split(","))
			        .map(String::trim)
			        .filter(StringUtils::hasText)
			        .distinct()
			        .collect(Collectors.toList());
	}

	// 영화 삭제
	@Transactional
	@Override
	public void deleteMovie(Long productNo) {
		Movie movie = movieRepository.findById(productNo)
			.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 영화입니다."));
		
		Product product = movie.getProduct();
		product.setProductDelFl(CommonEnums.ProductDelFl.Y);
	}
	
}
