package com.phobod.study.vcp.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.phobod.study.vcp.component.TestUtils;
import com.phobod.study.vcp.repository.storage.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class AuthentificationServiceTest {
	@InjectMocks
	private UserDetailsService userDetailsService = new AuthentificationService();

	@Mock
	private UserRepository userRepository;

	@Test(expected = UsernameNotFoundException.class)
	public final void testLoadUserByUsernameWithException() {
		when(userRepository.findByLogin("testLogin")).thenReturn(null);
		userDetailsService.loadUserByUsername("testLogin");
	}

	@Test
	public final void testLoadUserByUsernameSuccess() {
		when(userRepository.findByLogin(TestUtils.getTestUserWithoutId().getLogin())).thenReturn(TestUtils.getTestUserWithoutId());
		UserDetails userDetails = userDetailsService.loadUserByUsername(TestUtils.getTestUserWithoutId().getLogin());
		verify(userRepository).findByLogin(TestUtils.getTestUserWithoutId().getLogin());
		assertEquals(TestUtils.getTestUserWithoutId().getLogin(), userDetails.getUsername());
	}

}
