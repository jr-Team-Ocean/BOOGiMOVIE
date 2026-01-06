package com.bm.project.service.movie;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
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
import com.bm.project.elasticsearch.ProductDocument;
import com.bm.project.elasticsearch.ProductSearchRepository;
import com.bm.project.entity.Category;
import com.bm.project.entity.Movie;
import com.bm.project.entity.Product;
import com.bm.project.entity.ProductTag;
import com.bm.project.entity.ProductType;
import com.bm.project.entity.Review;
import com.bm.project.entity.TagCode;
import com.bm.project.enums.CommonEnums;
import com.bm.project.repository.CategoryRepository;
import com.bm.project.repository.LikeRepository;
import com.bm.project.repository.MovieRepository;
import com.bm.project.repository.ProductTypeRepository;
import com.bm.project.repository.ReviewRepository;
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
	private final LikeRepository likeRepository;
	private final ReviewRepository reviewRepository;
	private final ProductSearchRepository searchRepository;
	
	// private final String FILE_PATH = "C:\\bmImg\\movie\\";
	// private final String WEB_PATH = "/images/movie/";
	
	@Value("${my.movie.location}")
	private String FILE_PATH;
	
	@Value("${my.movie.webpath}")
	private String WEB_PATH;

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
        
        // 엘라스틱 저장로직
        ProductDocument doc = ProductDocument.builder()
                .productNo(product.getProductNo())            // 방금 저장된 PK
                .productTitle(product.getProductTitle())
                .productContent(product.getProductContent())
                .productPrice(product.getProductPrice())
                .productDate(product.getProductDate())
                .imgPath(product.getImgPath())
                
                .categoryName(category.getCategoryName()) // 카테고리명
                .productType("영화")                      // 타입 고정
                
                // ★ 핵심: 이미 List<String> 이니까 그대로 넣기!
                .directors(directors)     
                .publisher(companies)
                
                // 영화 관련 필드(directors, actors)는 도서니까 안 넣어도 됨 (null 처리됨)
                .build();

        searchRepository.save(doc); // 저장 쾅!

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
	        
	        // 엘라스틱 저장로직
	        ProductDocument doc = ProductDocument.builder()
	                .productNo(product.getProductNo())            // 방금 저장된 PK
	                .productTitle(product.getProductTitle())
	                .productContent(product.getProductContent())
	                .productPrice(product.getProductPrice())
	                .productDate(product.getProductDate())
	                .imgPath(product.getImgPath())
	                
	                .categoryName(category.getCategoryName()) // 카테고리명
	                .productType("영화")                      // 타입 고정
	                
	                // ★ 핵심: 이미 List<String> 이니까 그대로 넣기!
	                .directors(directors)     
	                .publisher(companies)
	                
	                // 영화 관련 필드(directors, actors)는 도서니까 안 넣어도 됨 (null 처리됨)
	                .build();

	        searchRepository.save(doc); // 저장 쾅!

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
	@Transactional(readOnly = false)
	@Override
	public void deleteMovie(Long productNo) {
		Movie movie = movieRepository.findById(productNo)
			.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 영화입니다."));
		
		Product product = movie.getProduct();
		product.setProductDelFl(CommonEnums.ProductDelFl.Y);
		
		// 엘라스틱 저장로직
        searchRepository.findById(productNo); // 삭제
		
	}
	
	// ======================================================================================================

	// 좋아요 처리
	@Override
	public int movieLike(Map<String, Long> likeMap) {
		
		Long memberNo = likeMap.get("memberNo");
		Long productNo = likeMap.get("productNo");
		Long check = likeMap.get("check");
		
		// 중복 방지
		boolean exists = likeRepository.existsByProduct_ProductNoAndMember_MemberNo(productNo, memberNo);
		System.out.println(exists);
		
		if(check == 1L) {
			
			if(!exists) {
				// 추가
				movieRepository.insertLike(productNo, memberNo);
				System.out.println("좋아요 추가!!!");
			}
		}else {
			if(exists) {
				// 삭제 처리
				likeRepository.deleteByProduct_ProductNoAndMember_MemberNo(productNo, memberNo);
				System.out.println("!!!좋아요 삭제!!!");
			}
		}
		
		return (int)likeRepository.countByProduct_ProductNo(productNo);
	}

	// 좋아요 여부 확인
	@Override
	public int movieLikeCheck(Long productNo, Long memberNo) {
		boolean exists = likeRepository.existsByProduct_ProductNoAndMember_MemberNo(productNo, memberNo);
		return exists ? 1 : 0;
	}

	// 좋아요 개수
	@Override
	public int movieLikeCount(Long productNo) {
		return likeRepository.countByProduct_ProductNo(productNo);
	}
	
	// ======================================================================================================

	// 후기 등록
	@Transactional(readOnly = false)
	@Override
	public int movieReviewWrite(Long productNo, Long memberNo, Integer reviewScore, String reviewContent) {
		return movieRepository.insertReview(productNo, memberNo, reviewScore, reviewContent);
	}

	// 후기 목록
	@Override
	public List<Review> selectReviewList(Long productNo) {
		return movieRepository.selectReviewList(productNo);
	}

	// 후기 수정
	@Transactional(readOnly = false)
	@Override
	public int updateReview(Long reviewNo, Long memberNo, String reviewContent) {
		
		Review review = reviewRepository.findByReviewNoAndMemberNo(reviewNo, memberNo)
											.orElseThrow(()-> new EntityNotFoundException("해당 리뷰가 없습니다."));
		
		if(review == null) return 0;
		
		review.setReviewContent(reviewContent);
		
		return 1;
	}

	// 후기 삭제
	@Transactional(readOnly = false)
	@Override
	public int deleteReview(Long reviewNo, Long memberNo) {
		reviewRepository.deleteByReviewNoAndMemberNo(reviewNo, memberNo);
		return 1;
	}
	
}
