package com.phobod.study.vcp.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.exception.CantSaveUserException;
import com.phobod.study.vcp.repository.storage.CompanyRepository;
import com.phobod.study.vcp.repository.storage.UserRepository;
import com.phobod.study.vcp.service.AdminService;
import com.phobod.study.vcp.service.ImageService;
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
	private ImageService imageService;
	
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
	public User saveUser(String userJson, MultipartFile avatar) {
		User user = parseUserFromJson(userJson);
		if (user.getId() == null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		String avatarImageUrl = saveImage(avatar);
		if (avatarImageUrl != null) {
			user.setAvatarUrl(avatarImageUrl);
		}
		return userRepository.save(user);
	}

	private User parseUserFromJson(String userJson) throws CantSaveUserException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(userJson, User.class);
		} catch (IOException e) {
			throw new CantSaveUserException("Can't parse user data: " + e.getMessage(), e);
		}
	}

	private String saveImage(MultipartFile avatar) throws CantSaveUserException {
		try {
			return saveImageInteranl(avatar);
		} catch (Exception e) {
			throw new CantSaveUserException("Can't save image data: " + e.getMessage(), e);
		}
	}

	private String saveImageInteranl(MultipartFile avatar) throws IOException {
		String avatarImageUrl = null;
		if (avatar != null) {
			avatarImageUrl = imageService.saveImageData(avatar.getBytes());
		}
		return avatarImageUrl;
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
