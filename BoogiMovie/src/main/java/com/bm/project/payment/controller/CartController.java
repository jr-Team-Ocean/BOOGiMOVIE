package com.bm.project.payment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/myPage")
public class CartController {
	
	// 장바구니로 이동
	@GetMapping("/cart")
	public String fowardCart() {
		return "cart_order/cart";
	}

}
