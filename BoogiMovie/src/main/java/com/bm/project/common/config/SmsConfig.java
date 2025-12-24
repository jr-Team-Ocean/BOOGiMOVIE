package com.bm.project.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.solapi.sdk.message.service.DefaultMessageService;

public class SmsConfig {

	@Value("${solapi.api-key}")
	private String apiKey;
	
	@Value("${solapi.api-secret}")
	private String apiSecret;
	
	
}
