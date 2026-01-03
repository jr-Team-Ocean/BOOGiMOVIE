package com.bm.project.payment.model.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bm.project.dto.MemberDto.LoginResult;
import com.bm.project.payment.entity.Cart;
import com.bm.project.payment.model.dto.CartDto;
import com.bm.project.payment.model.dto.CartDto.CartRespDto;
import com.bm.project.payment.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
	
	private final CartRepository cartRepo;
	

	// 장바구니 목록 조회
	@Override
	public List<CartRespDto> findCartListByMemberNo(LoginResult loginMember) {
		return cartRepo.findCartListByMemberNo(loginMember.getMemberNo())
				.stream()
				.map(CartDto.CartRespDto::convertToCartDto)
				.collect(Collectors.toList());
	}


	// 장바구니 아이템 삭제
	@Override
	public void deleteCartItem(List<Long> itemList) {
		cartRepo.deleteAllById(itemList);
		
	}


	// 장바구니 아이템 수량 변경
	@Override
	@Transactional // 변경 감지
	public void updateQuantity(Long itemNo, Integer quantity) {
		Cart cart = cartRepo.findById(itemNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템 번호입니다."));
		
		cart.updateQuantity(quantity);
	}

}
