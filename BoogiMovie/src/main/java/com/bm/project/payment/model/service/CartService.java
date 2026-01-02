package com.bm.project.payment.model.service;

import java.util.List;

import com.bm.project.dto.MemberDto.LoginResult;
import com.bm.project.payment.model.dto.CartDto.CartRespDto;

public interface CartService {

	/** 장바구니 목록 조회
	 * @param loginMember
	 * @return
	 */
	List<CartRespDto> findCartListByMemberNo(LoginResult loginMember);

}
