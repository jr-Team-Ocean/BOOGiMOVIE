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
		private String member_id;
		private String member_pw;
		private String member_name;
		private String birth_date;
		private String phone;
		private String nick_name;
		private String email;
		private String address;
		
		
		public Member toEntity() {
			return Member.builder()
					.memberId(this.member_id)
					.memberPw(this.member_pw)
					.memberName(this.member_name)
					.memberBirth(this.birth_date)
					.memberEmail(this.email)
					.memberNickName(this.nick_name)
					.memberAddress(this.address)
					.memberPhone(this.phone)
					.build();
		}
		
	}
	
	// 중복 검사
	@Getter
    @AllArgsConstructor
    public static class DupCheckResponse {
        private boolean available;
    }
	
}
