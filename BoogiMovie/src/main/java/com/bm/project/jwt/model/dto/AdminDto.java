package com.bm.project.jwt.model.dto;

import com.bm.project.entity.Member;
import com.bm.project.entity.Member.IsYN;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class AdminDto {
	
	// 관리자 로그인 DTO
	@Getter @Setter @Builder @ToString
	public static class AdminResponse {
		private String adminId; 	  // 로그인에 쓰는 입력 아이디
		private String adminPw;		  // 관리자 비밀번호
		
	}

	// 로그인 후 관리자 정보 담은 DTO
	@Getter @Setter @Builder
	public static class AdminInfo {
		private Long adminNo; 		  // 식별 번호
		private String adminId; 	  // 로그인에 쓰는 입력 아이디
		private String adminNickName; // "관리자"
		private String img;			  // 프로필 사진	
		private IsYN isAdmin;		  // 관리자 여부
		
		// 관리자 로그인 후 정보 담기
		public static AdminInfo adminInfo(Member member) {
			return AdminInfo.builder()
					.adminNo(member.getMemberNo())
					.adminId(member.getMemberId())
					.adminNickName(member.getMemberNickName())
					.img(member.getProfilePath())
					.isAdmin(member.getIsAdmin())
					.build();
		}
	}
	


}
