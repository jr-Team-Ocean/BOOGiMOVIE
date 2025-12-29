package com.bm.project.common.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bm.project.jwt.provider.JwtTokenProvider;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/* JwtFilter
 * 인증 필터 체인에서 UsernamePasswordAuthenticationFilter
 * 이전에 동작하는 커스텀 필터.
 * 
 * 클라이언트 요청에서 JWT를 검증하고, 유효한 토큰이면 해당 토큰에서 추출한
 * 사용자 인증 정보를 SecurityContext에 저장하여 인증 상태 유지
 * => 이를 통해 인증된 사용자의 요청만 처리할 수 있도록 함
 * 
 * SecurityContext
 * - Authentication 객체가 저장되는 보관소
 * - 해당 보관소는 ThreadLocal에 저장되어 같은 Thred라면 어디서든 접근할 수 있도록 설계 되어있음
 * - 인증이 완료되면 HttpSession에 저장된다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
	/*
	 * OncePerRequestFilter를 상속 받은 이유 - 이 필터가 요청당 한 번씩만 실행되도록 하기 위함
	 */

	private final JwtTokenProvider jwtTokenProvider;

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer ";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// JWT 토큰 추출 메소드 호출
		String accessToken = resolveToken((HttpServletRequest) request);
		
		if(accessToken != null) {
			
			try {
				// AccessToken 유효성 검사
				if (jwtTokenProvider.validateToken(accessToken)) {
					// 토큰이 유효할 경우, 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
					Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
					
					SecurityContextHolder.getContext().setAuthentication(authentication);
					System.out.println(authentication);
					// => 현재 실행 중인 Thread에 인증 정보 저장
				}
				
			} catch (ExpiredJwtException e) {
				// AccessToken이 유효하지 않는 경우 RefreshToken을 통해 재발급
				log.info("AccessToken이 만료 되었으므로, 재발급 합니다.");
				
				String refreshToken = null;
				
				// 쿠키에 저장된 refreshToken 가져오기
				Cookie[] cookies = request.getCookies();
				
				if(cookies != null) {
					for(Cookie c : cookies) {
						// 저장된 쿠키 중에 이름이 refreshToken가 있다면
						if("refreshToken".equals(c.getName())) {
							refreshToken = c.getValue(); // 토큰 값 저장
						}
					}
				}
				
				
				if(refreshToken != null &&
						jwtTokenProvider.validateRefreshToken(refreshToken)) {
					// Redis에서 리프레시 토큰과 비교했을 때 true 값을 반환 받았다면
					String auth = (String) e.getClaims().get("auth");
					String username = e.getClaims().getSubject();
					
					// 새 AccessToken 발급
					String newAccessToken = jwtTokenProvider.reissueAccessToken(username, auth);
					
					Authentication authentication = jwtTokenProvider.getAuthentication(newAccessToken);
					SecurityContextHolder.getContext().setAuthentication(authentication);
					
					// 쿠키 새로 교체
					Cookie cookie = new Cookie("accessToken", newAccessToken);
					cookie.setHttpOnly(true);  // 자바스크립트 접근 방지
					cookie.setPath("/"); 	   // 모든 경로에서 쿠키 전송
					cookie.setMaxAge(60 * 60); // 1시간
					response.addCookie(cookie);
					
					log.info("AccessToken 재발급 성공");
					
				}
						
			}
			
		}


		filterChain.doFilter(request, response); // 다음 필터로
	}

	// Request Header에서 JWT 토큰 추출
	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER); // 헤더에 붙은 문자열

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7); // "Bearer " 이후만 넘김 (주의: 띄어쓰기 포함)
		}

		// 헤더에 토큰이 없다면 컨트롤러에서 세팅한 쿠키값 가져오기
		Cookie[] cookies = request.getCookies(); // 쿠키들 가져오기
		if (cookies != null) {
			for (Cookie c : cookies) {
				if ("accessToken".equals(c.getName())) {
					return c.getValue();
				}
			}
		}

		return null;
	}

}
