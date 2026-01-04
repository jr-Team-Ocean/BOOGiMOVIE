package com.bm.project.payment.model.dto;

// 네이티브 쿼리를 위해 필요
public interface ICartDto {
	
	Long getCartNo();
    Long getMemberNo();
    Long getProductNo();
    Integer getQuantity();
    
    // Product 테이블에서 가져온 컬럼
    String getProductTitle();
    Integer getProductPrice();
    String getImgPath();
    Integer getTypeCode();

}
