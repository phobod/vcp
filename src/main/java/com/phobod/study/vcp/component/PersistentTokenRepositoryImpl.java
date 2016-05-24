package com.phobod.study.vcp.component;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

import com.phobod.study.vcp.domain.Token;
import com.phobod.study.vcp.repository.storage.TokenRepository;

@Component
public class PersistentTokenRepositoryImpl implements PersistentTokenRepository {

	@Autowired
	TokenRepository repository;

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		repository.save(new Token(null, token.getUsername(), token.getSeries(), token.getTokenValue(), token.getDate()));
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		Token token = repository.findBySeries(series);
		repository.save(new Token(token.getId(), token.getUsername(), series, tokenValue, lastUsed));
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		return repository.findBySeries(seriesId);
	}

	@Override
	public void removeUserTokens(String username) {
		List<Token> tokens = repository.findByUsername(username);
		repository.delete(tokens);
	}

}
