package com.phobod.study.vcp.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.repository.storage.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class AuthentificationServiceTest {
	@InjectMocks
	private UserDetailsService userDetailsService = new AuthentificationService();
	
	@Mock
	private UserRepository userRepository;
	
	private String login;
	private User user;
	
	@Before
	public void setUp() throws Exception {
		login = "testLogin";
		user = new User();
		user.setLogin(login);
	}

	@Test(expected = UsernameNotFoundException.class)
	public final void testLoadUserByUsernameWithException() {
		when(userRepository.findByLogin(login)).thenReturn(null);
		userDetailsService.loadUserByUsername(login);
	}

	@Test
	public final void testLoadUserByUsernameSuccess() {
		when(userRepository.findByLogin(login)).thenReturn(user);
		UserDetails userDetails = userDetailsService.loadUserByUsername(login);
		verify(userRepository).findByLogin(login);
		assertEquals(login, userDetails.getUsername());
	}
	
}
