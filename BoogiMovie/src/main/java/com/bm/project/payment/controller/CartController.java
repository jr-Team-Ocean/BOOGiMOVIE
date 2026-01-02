package com.bm.project.payment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.bm.project.dto.MemberDto;
import com.bm.project.payment.model.dto.CartDto;
import com.bm.project.payment.model.service.CartService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {
	
	@Value("${portone.store-id}") 
    private String storeId;

    @Value("${portone.channel-key}")
    private String channelKey;
    
    private final CartService service;
	
	// 장바구니 조회
	@GetMapping("/cart")
	public String getCartList(@SessionAttribute("loginMember") MemberDto.LoginResult loginMember,
							  Model model) {
		
		// 로그인한 회원이 가지고 있는 장바구니 목록 조회
		List<CartDto.CartRespDto> cartList = service.findCartListByMemberNo(loginMember);
		System.out.println("=== 장바구니 목록 조회 ===\n" + cartList);
		
		model.addAttribute("cartList", cartList);
		model.addAttribute("storeId", storeId);
		model.addAttribute("channelKey", channelKey);
		
		return "cart_order/cart";
	}

}
