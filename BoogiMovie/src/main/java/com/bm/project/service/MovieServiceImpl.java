package com.bm.project.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bm.project.dto.MovieDto;
import com.bm.project.dto.MovieDto.Response;
import com.bm.project.dto.PageDto;
import com.bm.project.entity.Movie;
import com.bm.project.repository.MovieRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieServiceImpl implements MovieService{
	
	private final MovieRepository movieRepository;

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

	@Override
	public Response getMovieDetail(Long productNo) {
		
		// 해당 영화 정보가 존재하는지 조회
		Movie movie = movieRepository.findById(productNo)
						.orElseThrow(() -> new EntityNotFoundException("해당 상품이 존재하지 않습니다."));
		return MovieDto.Response.toDto(movie);
	}


}
