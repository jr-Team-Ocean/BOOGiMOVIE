package com.bm.project.payment.model.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bm.project.dto.MemberDto.LoginResult;
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

}
