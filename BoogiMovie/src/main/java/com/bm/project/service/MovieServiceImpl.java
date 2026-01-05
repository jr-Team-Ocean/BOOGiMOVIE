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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bm.project.dto.MovieDto;
import com.bm.project.dto.MovieDto.Create;
import com.bm.project.dto.MovieDto.Response;
import com.bm.project.dto.PageDto;
import com.bm.project.entity.Category;
import com.bm.project.entity.Movie;
import com.bm.project.entity.Product;
import com.bm.project.entity.ProductTag;
import com.bm.project.entity.ProductType;
import com.bm.project.entity.TagCode;
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
	
}
