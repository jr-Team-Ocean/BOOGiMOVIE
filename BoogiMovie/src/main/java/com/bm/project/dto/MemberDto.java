package com.bm.project.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.bm.project.entity.Member;
import com.bm.project.entity.MemberSocial;
import com.bm.project.entity.Member.IsYN;
import com.bm.project.enums.CommonEnums.SocialProvider;

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
		private Long memberNo;
		private String memberId;
		private String memberNickName;
		private String enrollDate;
		private String memberPhone;
		private String profilePath;
		
		// Entity -> DTO
		public static MemberInfo infoToDto(Member member) {
			return MemberInfo.builder()
			.memberNo(member.getMemberNo())
			.memberId(member.getMemberId())
			.memberNickName(member.getMemberNickName())
			.enrollDate(member.getEnrollDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd 가입"))) // 2026-01-01 가입
			.memberPhone(member.getMemberPhone())
			.profilePath(member.getProfilePath())
			.build();
		}
		
	}
	
}
