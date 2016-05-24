package com.phobod.study.vcp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.repository.storage.UserRepository;
import com.phobod.study.vcp.security.CurrentUser;

@Service
public class AuthentificationService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		User user = userRepository.findByLogin(login);
		if (user == null) {
			throw new UsernameNotFoundException("User not found by login: " + login);
		}
		return new CurrentUser(user);
	}

}
