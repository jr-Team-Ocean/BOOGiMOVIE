package com.bm.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bm.project.jwt.provider.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity // Spring Security 활성화 어노테이션
@RequiredArgsConstructor
public class SecurityConfig {
	
//	private final JwtTokenProvider jwtTokenProvider;
	private final JwtFilter filter;
	
	/* filterChain 두 개인 이유
	 * 1. 관리자는 JWT 사용으로 인해 세션을 사용하지 않는다.
	 * 2. 반면 회원은 세션을 사용해야 한다.
	 * -> 두 갈래로 나누기 위해 우선순위(Order) 나눠서 검사를 차례대로 함
	 */
	
	// 관리자용 시큐리티 설정
	@Bean
	@Order(1) // 우선순위 첫 번째
	SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
		http
			.formLogin(form -> form.disable())
			.securityMatcher("/admin/**") // "/admin" 으로 시작하는 요첨만 받을 것
			.csrf(csrf -> csrf.disable()) // Cross-Site Request Forgery (사이트 간 요청 위조)
			
			// 관리자는 세션 사용 X
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		
			.authorizeHttpRequests(auth -> auth
			    // 관리자 로그인 주소만 빼서 누구나 접근 가능하도록 함
				.requestMatchers("/admin/login").permitAll()
					
				// 관리자 관련 페이지는 인증 필요
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().permitAll() // 나머지 모두 다 접근 가능
			)
			
			// JWT 필터 추가
			.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
			
		return http.build();
			
			
	}
	
	// 일반 회원용 시큐리티 설정
	@Bean
	@Order(2)
	SecurityFilterChain memberFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            
            // 일반 회원은 세션 사용
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
            		
            		// 로그인 해야만 접근할 수 있는 곳 (글쓰기 등 경로 더 작성하면 됨)
            		.requestMatchers("/member/**"
            				).authenticated()
            		
            		.anyRequest().permitAll() // 위 경로 제외 다른 곳들은 자유롭게 접근 가능
        		)
            .formLogin(form -> form
    				.loginPage("/member/login")       // 커스텀 로그인 페이지
    				.loginProcessingUrl("/member/login") // 로그인 처리 URL
    				.defaultSuccessUrl("/")           // 성공 시 메인으로
    				.permitAll()
    			);

        return http.build();
        
    }
	
}
