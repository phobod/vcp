package com.phobod.study.vcp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import com.phobod.study.vcp.domain.VideoStatistics;

@Configuration
@EnableRedisRepositories("com.phobod.study.vcp.repository.statistics")
public class RedisConfig {

	@Value("${redis.host}")
	private String redisHost;

	@Value("${redis.port}")
	private int redisPort;

	@Bean
	public RedisConnectionFactory connectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setHostName(redisHost);
		jedisConnectionFactory.setPort(redisPort);
		jedisConnectionFactory.setUsePool(true);
		return jedisConnectionFactory;
	}

	@Bean
	public RedisTemplate<String, VideoStatistics> redisTemplate() {
		RedisTemplate<String, VideoStatistics> template = new RedisTemplate<String, VideoStatistics>();
		template.setConnectionFactory(connectionFactory());
		return template;
	}
}
