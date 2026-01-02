package com.bm.project.service.myPage;

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

}
