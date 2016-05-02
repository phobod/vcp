package com.phobod.study.vcp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.repository.storage.UserRepository;
import com.phobod.study.vcp.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public Page<User> listAllUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}
}
