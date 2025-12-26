package com.bm.project.dto;

import com.bm.project.entity.Member;

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
		private String birth;
		private String phone;
		private String nickName;
		private String email;
		private String address;
		
		
		public Member toEntity() {
			return Member.builder()
					.memberId(this.memberId)
					.memberPw(this.memberPw)
					.memberName(this.memberName)
					.memberBirth(this.birth)
					.memberEmail(this.email)
					.memberNickName(this.nickName)
					.memberAddress(this.address)
					.memberPhone(this.phone)
					.build();
		}
		
	}
	
}
