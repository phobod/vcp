package com.phobod.study.vcp.repository.storage;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.phobod.study.vcp.domain.Token;

public interface TokenRepository extends MongoRepository<Token, String>{
	Token findBySeries(String series);
	List<Token> findByUsername(String username);
}
