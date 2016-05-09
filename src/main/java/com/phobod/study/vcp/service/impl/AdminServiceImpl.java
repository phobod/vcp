package com.phobod.study.vcp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.repository.storage.CompanyRepository;
import com.phobod.study.vcp.repository.storage.UserRepository;
import com.phobod.study.vcp.service.AdminService;
import com.phobod.study.vcp.service.UserService;

@Service
public class AdminServiceImpl implements AdminService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public Page<User> listAllUsers(Pageable pageable) {
		return userRepository.findAllByOrderByNameAsc(pageable);
	}

	@Override
	public Page<Company> listAllCompanies(Pageable pageable) {
		return companyRepository.findAllByOrderByNameAsc(pageable);
	}

	@Override
	public User saveUser(User user) {
		if (user.getId() == null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		return userRepository.save(user);
	}

	@Override
	public Company saveCompany(Company company) {
		Company cc = companyRepository.save(company);
		return cc;
	}

	@Override
	public void deleteUser(String userId) {
		userService.deleteAllVideosByUser(userId);
		userRepository.delete(userId);
	}

	@Override
	public void deleteCompany(String companyId) {
		List<User> listUsers = userRepository.findByCompanyId(companyId);
		for (User user : listUsers) {
			deleteUser(user.getId());
		}
		companyRepository.delete(companyId);
	}


}
