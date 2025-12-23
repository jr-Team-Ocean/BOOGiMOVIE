package com.bm.project.jwt.provider;

import java.security.Key;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.bm.project.jwt.model.dao.RedisDao;
import com.bm.project.jwt.model.service.AdminService;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

/*
 * JWT를 생성하고, 검증하는 등 핵심 기능 제공하는 클래스 
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	private final Key key; // signature에 들어갈 위조 방지 키
	private final AdminService adminService;
	private final RedisDao redisDao; // 리프레시 토큰 저장
	
	private static final String GRANT_TYPE = "Bearer";
	
	// application-secret.yml 값 읽어옴
	// application.yml에서 include: secret 해놨기 때문에 같이 불러와진다.
	@Value("${jwt.access-token-validity-in-seconds}") // 액세스 토큰 유효시간 1시간
	private long ACCESS_TOKEN_EXPIRE_TIME;
	
	@Value("${jwt.refresh-token-validity-in-seconds}") // 리프레시 토큰 유효시간 14일
	private long REFRESH_TOKEN_EXPIRE_TIME;

	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, 
							AdminService adminService,
							RedisDao redisDao) {
		
		// Base64로 인코딩된 시크릿 키를 다시 디코딩 하여 저장 (평문인 경우에는 반대로 Encoder)
		// 현재 저장된 시크릿 키는 openssl rand -base64 64로 만들어짐!
		byte[] keyBytes = Base64.getDecoder().decode(secretKey);
		
		// 디코딩한 값으로 JWT 서명을 위한 Key 객체 생성
		this.key = Keys.hmacShaKeyFor(keyBytes);
		
		this.adminService = adminService;
		this.redisDao = redisDao;
	}
	
	
	
}
