package com.bm.project.dto;

import com.bm.project.entity.Member;
import com.bm.project.entity.MemberSocial;
import com.bm.project.entity.Member.IsYN;
import com.bm.project.enums.CommonEnums.SocialProvider;

import lombok.AllArgsConstructor;
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
	
}
