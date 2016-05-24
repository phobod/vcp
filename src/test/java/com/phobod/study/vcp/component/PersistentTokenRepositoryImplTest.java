package com.phobod.study.vcp.component;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.phobod.study.vcp.domain.Token;
import com.phobod.study.vcp.repository.storage.TokenRepository;

@RunWith(MockitoJUnitRunner.class)
public class PersistentTokenRepositoryImplTest {
	@InjectMocks
	private PersistentTokenRepository persistentTokenRepository = new PersistentTokenRepositoryImpl();

	@Mock
	TokenRepository repository;

	private PersistentRememberMeToken token;
	private String tokenId;
	private String userName;
	private String series;
	private String tokenValue;
	private Date date;
	private Token oldToken;

	@Before
	public void setUp() throws Exception {
		tokenId = "tokenId";
		userName = "username";
		series = "series";
		tokenValue = "tokenValue";
		date = new Date();
		token = new PersistentRememberMeToken(userName, series, tokenValue, date);
		oldToken = new Token(tokenId, userName, null, null, null);
	}

	@Test
	public final void testCreateNewToken() {
		persistentTokenRepository.createNewToken(token);
		verify(repository).save(any(Token.class));
	}

	@Test
	public final void testUpdateToken() {
		when(repository.findBySeries(series)).thenReturn(oldToken);
		persistentTokenRepository.updateToken(series, tokenValue, date);
		verify(repository).findBySeries(series);
		verify(repository).save(argThat(new TokenArgumentMatcher()));
	}

	@Test
	public final void testGetTokenForSeries() {
		persistentTokenRepository.getTokenForSeries(series);
		verify(repository).findBySeries(series);
	}

	@Test
	public final void testRemoveUserTokens() {
		List<Token> list = new ArrayList<>();
		when(repository.findByUsername(userName)).thenReturn(list);
		persistentTokenRepository.removeUserTokens(userName);
		verify(repository).findByUsername(userName);
		verify(repository).delete(list);
	}

	private class TokenArgumentMatcher extends ArgumentMatcher<Token> {
		@Override
		public boolean matches(Object argument) {
			if (argument instanceof Token) {
				Token token = (Token) argument;
				if (tokenId.equals(token.getId()) && userName.equals(token.getUsername()) && series.equals(token.getSeries()) && tokenValue.equals(token.getTokenValue())) {
					return true;
				}
			}
			return false;
		}
	}
}
