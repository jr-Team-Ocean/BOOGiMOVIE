package com.bm.project.payment.model.service;

import java.util.List;

import com.bm.project.dto.MemberDto.LoginResult;
import com.bm.project.dto.MemberDto.OrderMemberDto;
import com.bm.project.payment.model.dto.CartDto.CartRespDto;

public interface CartService {

	/** 장바구니 목록 조회
	 * @param loginMember
	 * @return
	 */
	List<CartRespDto> findCartListByMemberNo(LoginResult loginMember);

	/** 장바구니 아이템 삭제
	 * @param itemList
	 */
	void deleteCartItem(List<Long> itemList);

	/** 장바구니 아이템 수량 변경
	 * @param itemNo
	 * @param quantity
	 */
	void updateQuantity(Long itemNo, Integer quantity);

	OrderMemberDto getOrderMemberInfo(Long memberNo);

	/** 장바구니에 담?기
	 * @param productNo
	 * @param quantity
	 * @param memberNo
	 */
	void addCart(Long productNo, Integer quantity, Long memberNo);
	
}
