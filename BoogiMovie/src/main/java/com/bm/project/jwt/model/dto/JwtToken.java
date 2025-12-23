package com.bm.project.jwt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class JwtToken {
	private String grantType; 		// JWT에 대한 인증 타입, Bearer 인증 방식 사용 예정
	private String accessToken; 	// 액세스 토큰
	private String refreshToken;    // 리프레시 토큰
	
	/* Bearer Token (무기명 토큰)
	 * "이 토큰의 소유자를 인증하라"
	 * 
	 * - Oauth 2.0 프로토콜에서 사용되는 토큰이며, 인증된 사용자를 대신해 API에 접근할 수 있는 권한 부여
	 * - JWT 토큰과 달리, 토큰 내에 사용자 정보를 포함시키지 않는다.
	 * 
	 * => JWT 토큰은 사용자 정보와 권한 정보를 포함시켜 전달하며, 
	 *    Bearer 토큰은 인증된 사용자를 대신하여 API에 접근할 수 있는 권한을 부여한다.
	 * */
}
