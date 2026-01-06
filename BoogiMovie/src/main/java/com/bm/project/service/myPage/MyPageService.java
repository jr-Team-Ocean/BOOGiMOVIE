package com.bm.project.service.myPage;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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

	
	/** 회원 정보 수정
	 * @param memberNo
	 * @param item
	 * @param value
	 */
	void changeProfileInfo(Long memberNo, String item, String value);

	/** 비밀번호 변경
	 * @param memberNo
	 * @param newPw
	 */
	void changeProfilePw(Long memberNo, String newPw);

	/** 프로필 이미지 변경
	 * @param memberNo
	 * @param profileImage
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	void changeProfileImg(Long memberNo, MultipartFile profileImage) throws IllegalStateException, IOException;

}
