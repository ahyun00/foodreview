package org.foodreview.config;

import org.foodreview.interceptor.AdminInterceptor;
import org.foodreview.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	
	private final LoginInterceptor loginInterceptor;
	private final AdminInterceptor adminInterceptor;
	
	// 인터셉터를 URL에 등록하는 메서드
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		// 일반 회원 로그인 검사
		registry.addInterceptor(loginInterceptor)
					// 로그인 검사가 필요한 URL 지정
					.addPathPatterns( 
						"/mypage", 
						"/mypage/**", 
						"/reviews/write", 
						"/reviews/*/edit",  
						"/reviews/*/delete",
						"/visits/register"
					);
		
		 // 관리자 로그인 + 권한 검사
		registry.addInterceptor(adminInterceptor)
					.addPathPatterns(
							"/admin",
							"/admin/**",
							"/api/admin/**"
					);
	}

}
















