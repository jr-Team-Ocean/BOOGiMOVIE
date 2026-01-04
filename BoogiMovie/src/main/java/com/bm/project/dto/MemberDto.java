package com.bm.project.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.bm.project.entity.Member;
import com.bm.project.entity.MemberSocial;
import com.bm.project.entity.Product;
import com.bm.project.entity.Member.IsYN;
import com.bm.project.enums.CommonEnums.SocialProvider;
import com.bm.project.payment.entity.Orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class MemberDto {

	// 회원 등록
	@Getter
	@Setter
	@ToString
	public static class Create{
		private String memberId;
		private String memberPw;
		private String memberName;
		private String memberBirth;
		private String memberPhone;
		private String memberNickName;
		private String memberEmail;
		private String memberAddress;
		
		
		public Member toEntity() {
			return Member.builder()
					.memberId(this.memberId)
					.memberPw(this.memberPw)
					.memberName(this.memberName)
					.memberBirth(this.memberBirth)
					.memberEmail(this.memberEmail)
					.memberNickName(this.memberNickName)
					.memberAddress(this.memberAddress)
					.memberPhone(this.memberPhone)
					.build();
		}
		
	}
	
	// 로그인 요청
	@Getter
	@Setter
	public static class Login{
		private String memberId;
		private String memberPw;
	}
	
	// 로그인 결과
	@Getter
	@Setter
	public static class LoginResult{
		private Long memberNo;
		private String memberId;
		private String memberNickName;
		private IsYN isAdmin;
		
		
		public static LoginResult fromEntity(Member m) {
			LoginResult dto = new LoginResult();
	        dto.setMemberNo(m.getMemberNo());
	        dto.setMemberId(m.getMemberId());
	        dto.setMemberNickName(m.getMemberNickName());
	        dto.setIsAdmin(m.getIsAdmin());
	        return dto;
	    }
		
	}
	
	// 소셜 로그인
	@Getter
	@ToString
	public static class SocialLogin{
		private SocialProvider provider;
		private String providerId;
		private String memberEmail;
		private String memberNickName;
		private String profilePath;
		
	}
	
	// 마이페이지에서 보여줄 회원 정보
	@Getter @Builder @ToString
	public static class MemberInfo {
		// 회원 정보
		private Long memberNo;
		private String memberId;
		private String memberNickName;
		private String enrollDate;
		private String memberPhone;
		private String profilePath;
		
		// 내가 소장한 영화
		private List<PurchasedMovie> myMovies;
		
		@Getter @Builder @ToString
	    public static class PurchasedMovie {
	        private Long productNo;
	        private String productTitle;
	        private String imgPath;
	    }
		
		// Entity -> DTO
		public static MemberInfo infoToDto(Member member, List<IMyPageDto> myMovies) {
			List<PurchasedMovie> movieDtos = myMovies.stream()
		            .map(product -> PurchasedMovie.builder()
		                .productNo(product.getProductNo())
		                .productTitle(product.getProductTitle())
		                .imgPath(product.getImgPath())
		                .build())
		            .collect(Collectors.toList());
			
			return MemberInfo.builder()
			.memberNo(member.getMemberNo())
			.memberId(member.getMemberId())
			.memberNickName(member.getMemberNickName())
			.enrollDate(member.getEnrollDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd 가입"))) // 2026-01-01 가입
			.memberPhone(member.getMemberPhone())
			.profilePath(member.getProfilePath())
			.myMovies(movieDtos) // 내가 소장한 영화
			.build();
		}
		
	}
	
		// 내가 찜한 상품 응답용 DTO
	    @Getter 
	    @Builder 
	    @ToString	 
        public static class FavoriteResponse {
	    	private Long productNo;        
	        private String productTitle;   
	        private String productAuthor;  // 추가 (오류 해결용)
	        private Integer productPrice;  
	        private String productStatus;  // 추가 (오류 해결용)
	        private String productImage;   // imgPath 대신 productImage로 통일 (화면과 맞춤)
	        private String favoriteDate;   

	        public static FavoriteResponse toDto(Product p) {
	            return FavoriteResponse.builder()
	                    .productNo(p.getProductNo())
	                    .productTitle(p.getProductTitle())
	                    .productAuthor("저자 정보 없음") // 이제 에러 안 남
	                    .productPrice(p.getProductPrice())
	                    .productStatus("판매중") // 이제 에러 안 남
	                    .productImage(p.getImgPath()) // 필드명을 productImage로 했으므로 이 메서드 사용
	                    .favoriteDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
	                    .build();
	        }
	    }
	
}
