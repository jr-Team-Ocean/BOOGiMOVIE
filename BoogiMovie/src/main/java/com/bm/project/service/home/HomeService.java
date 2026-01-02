package com.bm.project.service.home;

import java.util.List;

import com.bm.project.dto.HomeDto;

public interface HomeService {

	/** 결제 많은 도서/영화 목록 조회
	 * @return HomeDto
	 */
	List<HomeDto> getPopularProducts();

	/** 인기 도서 조회
	 * @return HomeDto
	 */
	List<HomeDto> getTopBooks();

	/** 인기 영화 조회
	 * @return HomeDto
	 */
	List<HomeDto> getTopMovies();


}
