package com.bm.project.payment.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/myPage")
public class CartController {
	
	@Value("${portone.store-id}") 
    private String storeId;

    @Value("${portone.channel-key}")
    private String channelKey;
	
	// 장바구니로 이동
	@GetMapping("/cart")
	public String fowardCart(Model model) {
		model.addAttribute("storeId", storeId);
		model.addAttribute("channelKey", channelKey);
		return "cart_order/cart";
	}

}
