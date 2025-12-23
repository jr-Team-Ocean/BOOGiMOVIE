package com.bm.project.jwt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class JwtToken {
	private String grantType; // JWT에 대한 인증 타입, Bearer 인증 방식 사용 예정
	private String accessToken;
	private String refreshToken;
}
