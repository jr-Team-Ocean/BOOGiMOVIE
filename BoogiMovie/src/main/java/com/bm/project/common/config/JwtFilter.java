package com.bm.project.common.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bm.project.jwt.provider.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

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
public class JwtFilter extends OncePerRequestFilter {
							/* OncePerRequestFilter를 상속 받은 이유
							 * - 이 필터가 요청당 한 번씩만 실행되도록 하기 위함
							 */
	
	private final JwtTokenProvider jwtTokenProvider;
	
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// JWT 토큰 추출 메소드 호출
		String accessToken = resolveToken((HttpServletRequest) request);
		
		// AccessToken 유효성 검사
		if(accessToken != null) {
			
			if(jwtTokenProvider.validateToken(accessToken)) {
				// 토큰이 유효할 경우, 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
				Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				// => 현재 실행 중인 Thread에 인증 정보 저장
				
			} else {
				// 토큰이 유효하지 않는 경우 필터 처리 X
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 인증 정보 부족
				return;
			}
		}
		
		filterChain.doFilter(request, response); // 다음 필터로
	}

	// Request Header에서 JWT 토큰 추출
	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER); // 헤더에 붙은 문자열
		if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7); // "Bearer " 이후만 넘김 (주의: 띄어쓰기 포함)
		}
		
		return null;
	}


}
