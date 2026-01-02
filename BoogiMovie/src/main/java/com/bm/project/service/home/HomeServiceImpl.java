package com.bm.project.service.home;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bm.project.dto.HomeDto;
import com.bm.project.dto.HomeDto.HomeLike;
import com.bm.project.repository.HomeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {
	
	private final HomeRepository homeRepo;
	
	// 결제 많은 도서/영화 목록 조회
	@Override
	public List<HomeDto.HomeOrder> getPopularProducts() {
		return homeRepo.getPopularProducts()
				.stream()
				.map(HomeDto.HomeOrder::convertToOrderDto)
				.collect(Collectors.toList());
	}

	// 인기 도서 조회
	@Override
	public List<HomeDto.HomeLike> getTopBooks() {
		// typeCode, creatorCode, companyCode
		return homeRepo.findTop5Products(1L, 1L, 3L)
				.stream() // HomeInterfaceDto를 흘려보냄
				.map(HomeDto.HomeLike::convertToLikeDto) // DTO로 변환
				.collect(Collectors.toList());
	}

	// 인기 영화 조회
	@Override
	public List<HomeDto.HomeLike> getTopMovies() {
		// typeCode, creatorCode, companyCode
		return homeRepo.findTop5Products(2L, 2L, 4L)
				.stream() // HomeInterfaceDto를 흘려보냄
				.map(HomeDto.HomeLike::convertToLikeDto) // DTO로 변환
				.collect(Collectors.toList());
	}


}
