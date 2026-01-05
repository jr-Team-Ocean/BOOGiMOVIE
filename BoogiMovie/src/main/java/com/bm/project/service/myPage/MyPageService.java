package com.bm.project.service.myPage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bm.project.dto.MemberDto.FavoriteResponse;
import com.bm.project.dto.MemberDto.MemberInfo;

public interface MyPageService {

	/** 회원 정보 가져오기
	 * @param memberNo
	 * @return
	 */
	MemberInfo getMemberInfo(Long memberNo);

	/** 회원 탈퇴
	 * @param memberNo
	 * @return
	 */
	int secession(Long memberNo);

	/**  내가 찜한 상품 
	 * @param memberNo
	 * @param string
	 * @param pageable
	 * @return
	 */
	Page<FavoriteResponse> getFavoriteList(Long memberNo, String string, Pageable pageable);

	
	/** 
	 * @param productNo
	 * @param memberNo
	 * @return
	 */
	boolean removeFavorite(int productNo, Long memberNo);

}
