package com.bm.project.common.config;

import org.apache.catalina.Context;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		
		MultipartConfigFactory factory = new MultipartConfigFactory();
		
		// yml 설정 그대로 설정해둠
		factory.setMaxFileSize(DataSize.ofMegabytes(10));
        factory.setMaxRequestSize(DataSize.ofMegabytes(50));
        factory.setFileSizeThreshold(DataSize.ofMegabytes(50));
		
        
        return factory.createMultipartConfig();
		
	}
	
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		// 이미지
		String webPath = "/images/**";
		
		// 실제 저장 경로
		String resourcePath = "file:///C:/bmImg/";
		
		// /images/ 로 시작하는 요청이 오면
		// C:/bmImg/ 와 연결해라
		registry.addResourceHandler(webPath).addResourceLocations(resourcePath);
	}
	
	
}
