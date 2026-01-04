package com.bm.project.enums;

public class CommonEnums {

	public enum ProductDelFl {
		Y, N
	}
	
	// 소셜로그인, 일반회원 구분
	public enum SocialProvider{
		LOCAL, GOOGLE, KAKAO
	}
	
	// 영화 관람등급
	public enum MovieRating{
		All("전체 관람"), 
		TWELVE("12세이상 관람"), 
		FIFTEEN("15세이상 관람"), 
		ADULT("청소년 관람불가");
		
		private final String description;

		MovieRating(String description) {
			this.description = description;
		}
		
		public String getDescripton() {
			return description;
		}
	}
	
	
}
