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
import lombok.NoArgsConstructor;
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
	
	// 결제창에서 보여줄 회원 정보
	@Getter @Setter @ToString @NoArgsConstructor
	public static class OrderMemberDto {
		// 이름, 전화번호, 주소 (우편번호, 도로명 주소, 상세 주소)
		private Long memberNo;
		private String memberName;
		private String memberPhone;
		private String postNo;
		private String address;
		private String detailAddress;
		
		public static OrderMemberDto orderMember(Member member) {
			OrderMemberDto dto = new OrderMemberDto();
			
			dto.setMemberNo(member.getMemberNo());
			dto.setMemberName(member.getMemberName());
			dto.setMemberPhone(member.getMemberPhone());
			
			// ^^^로 합쳐져서 저장된 주소를 하나씩 풀어서 각각 담기
			String fullAddress = member.getMemberAddress();
			String[] splitAddress = fullAddress.split("\\^\\^\\^"); // 캐럿은 정규표현식으로 들어가서 이스케이프 써야 함
			
			if(splitAddress.length >= 1) dto.setPostNo(splitAddress[0]);
			if(splitAddress.length >= 2) dto.setAddress(splitAddress[1]);
			if(splitAddress.length >= 3) dto.setDetailAddress(splitAddress[2]);
			
			return dto;
			
		}
	}
	
}
