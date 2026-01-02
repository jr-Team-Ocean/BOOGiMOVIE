package com.bm.project.dto;

// 네이티브 쿼리 사용시 필요
public interface IHomeLikeDto {
	
	Long getProductNo();
    String getProductTitle();
    String getImgPath();
    Long getLikeCount();
    
    // DB에서는 하나의 문자열로 넘어옴
    String getCreator(); 
    String getCompany();

}
