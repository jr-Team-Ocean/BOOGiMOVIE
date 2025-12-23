package com.bm.project.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration // 설정 파일
@EnableRedisRepositories // Redis 저장소 기능 활성화
public class RedisConfig {
	
    // application.yml에서 host, port 값을 주입하기
    @Value("${spring.data.redis.host}") // localhost
    private String host;

    @Value("${spring.data.redis.port}") // 6379
    private int port;
    
    // Redis 연결 팩토리 설정
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
    	// Redis 설정: host / port 필요
    	RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    	config.setHostName(host);
    	config.setPort(port);
    	
    	// Lettuce vs Jedis
    	// Lettuce가 Jedis보다 성능 좋고 비동기 처리 가능 (spring-boot-starter-data-redis 사용시 의존성 설정 없이 사용 가능)
    	return new LettuceConnectionFactory(config);
    }
    
    // RedisTemplate 설정
    // RedisTemplate: DB 서버에 Set / Get / Delete 등 사용할 수 있다.
    // 트랜잭션도 지원!
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
    	RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    	redisTemplate.setConnectionFactory(redisConnectionFactory());
    	
    	// 객체를 저장하기 때문에 직렬화 필요
    	// key, value에 대한 직렬화
    	redisTemplate.setKeySerializer(new StringRedisSerializer());
    	redisTemplate.setValueSerializer(new StringRedisSerializer());
    	
    	// hash key, hash value에 대한 직렬화
    	redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    	redisTemplate.setHashValueSerializer(new StringRedisSerializer());
    	
    	// => 일반 문자열과 hash는 데이터 생김새가 다르기 때문에 미리 두 구조 다 직렬화를 세팅해줌!
    	
    	return redisTemplate;
    	
    }
}
