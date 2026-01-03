package com.bm.project.payment.model.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


public class CartDto {

	// P.IMG_PATH AS imgPath, P.PRODUCT_TITLE AS productTitle,
	// P.PRODUCT_PRICE as productPrice, C.PRODUCT_NO AS productNo, 
	// QUANTITY AS quantity, MEMBER_NO AS memberNo

	// 장바구니 조회
	@Getter
	@Builder
	@ToString
	public static class CartRespDto {
		private Long cartNo;
		private Long productNo;
		private String productTitle;
		private String imgPath;
		private Integer productPrice;
		private Integer quantity;
		private Long memberNo;
		private Integer typeCode;
		
		// 인터페이스 -> DTO
		public static CartRespDto convertToCartDto(ICartDto raw) {
	        return CartRespDto.builder()
	        		.cartNo(raw.getCartNo()) // 장바구니 아이템 번호
	        		.productNo(raw.getProductNo())
	        		.productTitle(raw.getProductTitle())
	        		.imgPath(raw.getImgPath())
	        		.productPrice(raw.getProductPrice())
	        		.quantity(raw.getQuantity())
	        		.memberNo(raw.getMemberNo())
	        		.typeCode(raw.getTypeCode()) // 도서1, 영화2
	        		.build();
	    }
		
	}
}
