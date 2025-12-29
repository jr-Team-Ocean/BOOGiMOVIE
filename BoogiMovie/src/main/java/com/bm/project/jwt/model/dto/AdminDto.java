package com.bm.project.jwt.model.dto;

import com.bm.project.entity.Member;
import com.bm.project.entity.Member.IsYN;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class AdminDto {
	
	// 관리자 로그인 DTO
	@Getter
	@Setter
	@Builder
	public static class AdminResponse {
		private Long adminNo; 		  // 식별 번호
		private String adminId; 	  // 로그인에 쓰는 입력 아이디
		private String adminPw;		  // 관리자 비밀번호
		private String adminNickName; // "관리자"
		private IsYN isAdmin;		  // 관리자 여부
		
		// 관리자 로그인
		public static AdminResponse toDto(Member member) {
			return AdminResponse.builder()
					.adminNo(member.getMemberNo())
					.adminId(member.getMemberId())
					.adminNickName(member.getMemberNickName())
					.build();
		}
		
		
	}
	
	

	



}
