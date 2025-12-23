package com.bm.project.jwt.provider;

import java.security.Key;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.bm.project.jwt.model.dao.RedisDao;
import com.bm.project.jwt.model.dto.JwtToken;
import com.bm.project.jwt.model.service.AdminService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * JWT를 생성하고, 검증하는 등 핵심 기능 제공하는 클래스 
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
	private final AdminService adminService; // 서비스 클래스
	
	private final RedisDao redisDao; // 리프레시 토큰 저장
	
	private final Key key; // signature에 들어갈 위조 방지 키
	
	private static final String GRANT_TYPE = "Bearer";
	
	/***********************************************************************/
	
	// application-secret.yml 값 읽어옴
	// application.yml에서 include: secret 해놨기 때문에 같이 불러와진다.
	@Value("${jwt.access-token-expire-time}") // 액세스 토큰 유효시간 1시간
	private long ACCESS_TOKEN_EXPIRE_TIME;
	
	@Value("${jwt.refresh-token-expire-time}") // 리프레시 토큰 유효시간 14일
	private long REFRESH_TOKEN_EXPIRE_TIME;

	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, 
							AdminService adminService,
							RedisDao redisDao) {
		
		// Base64로 인코딩된 시크릿 키를 다시 디코딩 하여 저장 (평문인 경우에는 반대로 Encoder)
		// 현재 저장된 시크릿 키는 openssl rand -base64 64로 만들어짐!
		byte[] keyBytes = Base64.getDecoder().decode(secretKey);
		
		// 디코딩한 값으로 JWT 서명을 위한 Key 객체 생성
		this.key = Keys.hmacShaKeyFor(keyBytes); // 단방향 암호화
		// HAMC-SHA: 비밀 키를 사용하여 메세지의 무결성을 검증하는 해시 기반 인증 코드
		// 메세지와 키를 결합한 후 해시 값 생성
		
		this.adminService = adminService;
		this.redisDao = redisDao;
	}
	
	// 회원 정보를 가지고 AccessToken, RefreshToken 생성
	public JwtToken createToken(Authentication authentication) {
		// 권한 가져오기 (authorities에는 인증된 유저가 소유한 권한 목록이 저장되어 있다.)
		String authorities = authentication.getAuthorities().stream() // Authentication 객체에서 사용자 권한 목록 가져오기
				.map(GrantedAuthority :: getAuthority) // 권한 문자열만 추출해서
				.collect(Collectors.joining(","));	   // 문자열들을 쉼표로 구분하여 하나로 join
		
		long now = new Date().getTime();
		String username = authentication.getName();
		
		// AccessToken 생성
		Date accessTokenExpire = new Date(now + ACCESS_TOKEN_EXPIRE_TIME); // 만료일 지정
		// AccessToken 생성 메소드 호출 (유저명, 인증정보, 만료일)
		String accessToken = createAccessToken(username, authorities, accessTokenExpire);
		
		// RefreshToken 생성
		Date refreshTokenExpire = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
		// RefreshToken 생성 메소드 호출 (유저명, 만료일): 재발급 용도이므로 인증 정보 불필요
		String refreshToken = createRefreshToken(username, refreshTokenExpire);
		
		// redis에 refreshToken 저장, 지정한 만료일만큼 시간이 지나면 삭제
		redisDao.setValues(username, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));
		log.info("[Redis] : RefreshToken 저장");
		
		return JwtToken.builder()
				.grantType(GRANT_TYPE) // "Bearer" : 요청 보낼 때 헤더에 붙어서 보내짐
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}


	// AccessToken 생성 메소드
	private String createAccessToken(String username, String authorities, Date expireDate) {
		return Jwts.builder()
				.setSubject(username) 					 // 토큰 제목
				.claim("auth", authorities) 			 // 권한 정보 (커스텀 클레임)
				.setExpiration(expireDate)  			 // 토큰 만료 시간
				.signWith(key, SignatureAlgorithm.HS256) // 지정된 키와 알고리즘으로 서명
				.compact(); 
			    // JWT 문자열 생성 : (header.payload.signature)
	}
	
	// RefreshToken 생성 메소드
	private String createRefreshToken(String username, Date expireDate) {
		return Jwts.builder()
				.setSubject(username)
				.setExpiration(expireDate)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}
	
	// JWT 토큰을 복호화 하여 토큰에 들어있는 정보 꺼내기
	public Authentication getAuthentication(String accessToken) {
		// JWT 토큰 복호화 메소드 호출
		Claims claims = parseClames(accessToken);
		
		if(claims.get("auth") == null) {
			throw new RuntimeException("권한 정보가 없는 토큰입니다.");
		}
		
		// 클레임에서 권한 정보 가져오기
		Collection<? extends GrantedAuthority> authorities 
								= Arrays.stream(claims.get("auth").toString().split(","))
								.map(SimpleGrantedAuthority::new) // 각 문자열을 SimpleGrantedAuthority 권한 객체로 변환
								.toList();
		// Collection<? extends GrantedAuthority>: 컬렉션에 들어가는 자료형은 GrantedAuthority를 상속 받은 것들만!
		// 한 사람당(여기서는 Admin만) 권한은 항상 여러 개일 수 있다고 가정하에 Collection 형태로 들고 다닌다.
		
															// 사용자 pw 필요 x
		UserDetails principal = new User(claims.getSubject(), "", authorities);
		
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	// JWT 토큰 복호화 메소드
	private Claims parseClames(String accessToken) {
		try {
			
			return Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(accessToken) // JWT 토큰 검증 및 파싱 모두 수행
					// 토큰 형식 검증 + 서명 검증 + 만료시간 검증 => JWT 토큰을 header, body, signature 형태로 분리
					.getBody();
		
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}
	
	// 토큰 정보 검증
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token);
			
			return true;
			
		} catch (SecurityException | MalformedJwtException e) { // 둘 중 하나 터지면 잡음
			log.info("잘못된 JWT 서명입니다.");
			
		} catch (ExpiredJwtException e) {
			log.info("만료된 JWT 토큰입니다.");
			
		} catch (UnsupportedJwtException e) {
			log.info("지원되지 않는 JWT 토큰입니다.");
		
		} catch (IllegalArgumentException e) {
			log.info("JWT 토큰이 잘못 되었습니다.");
		}
		
		return false;
	}
	
	// RefreshToken 검증
	public boolean validateRefreshToken(String token) {
		// 기본적인 JWT 검증
		if(!validateToken(token)) return false;
		
		try {
			// token에서 username 추출
			String username = getUserName(token);
			
			// Redis에 저장된 RefreshToken과 비교
			String redisToken = (String) redisDao.getValues(username);
			return token.equals(redisToken);
			
		} catch (Exception e) {
			log.info("RefreshToken 검증 실패", e);
			return false;
		}
	}

	// 토큰에서 username 추출
	private String getUserName(String token) {
		try {
			// 토큰 파싱 후 클레임 얻기
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody();
			
			// 사용자명 반환
			return claims.getSubject();
		
		} catch (ExpiredJwtException e) {
			// 토큰이 만료 되어도 클레임 내용을 가져올 수 있다.
			return e.getClaims().getSubject();
		}
	}
	
	// 로그아웃 시 Redis에서 RefreshToken 삭제
	public void deleteRefreshToken(String username) {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("유저 이름이 비어있거나, 없습니다.");
		}
	
		redisDao.deleteValues(username);
	}
	
	
	
}
