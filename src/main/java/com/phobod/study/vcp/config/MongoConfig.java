package com.phobod.study.vcp.config;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories("com.phobod.study.vcp.repository.storage")
public class MongoConfig {
	
	@Value("${mongo.host}")
	private String mongoHost;
	
	@Value("${mongo.port}")
	private int mongoPort;
	
	public @Bean MongoClient mongo() throws UnknownHostException {
		return new MongoClient(mongoHost, mongoPort);
	}

	public @Bean MongoTemplate mongoTemplate(@Value("${mongo.db}") String mongoDb) throws Exception {
		MongoTemplate mongoTemplate = new MongoTemplate(mongo(), mongoDb);
		return mongoTemplate;
	}
}
