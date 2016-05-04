package com.phobod.study.vcp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.form.CompaniesUploadForm;
import com.phobod.study.vcp.form.UsersUploadForm;
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
	
	@Override
	public Page<User> listAllUsers(Pageable pageable) {
		return userRepository.findAllByOrderByNameAsc(pageable);
	}

	@Override
	public Page<Company> listAllCompanies(Pageable pageable) {
		return companyRepository.findAllByOrderByNameAsc(pageable);
	}

	@Override
	public List<Company> listAllCompanies() {
		return companyRepository.findAllByOrderByNameAsc();
	}
	
	@Override
	public User addUser(UsersUploadForm form) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String avatarImageUrl = null;
		if (form.getAvatar() != null) {
			avatarImageUrl = imageService.saveImageData(form.getAvatar());		
		}
		Company company = companyRepository.findOne(form.getCompanyId());
		User user = new User(form.getName(), form.getSurname(), form.getLogin(), encoder.encode(form.getPassword()), form.getEmail(), company, form.getRole(), avatarImageUrl);
		return userRepository.save(user);
	}

	@Override
	public Company addCompany(CompaniesUploadForm form) {
		Company company = new Company(form.getName(), form.getAddress(), form.getEmail(), form.getPhone());
		return companyRepository.save(company);
	}

	@Override
	public User updateUser(UsersUploadForm form, String userId) {
		Company company = companyRepository.findOne(form.getCompanyId());
		User user = userRepository.findOne(userId);
		user.setName(form.getName());
		user.setSurname(form.getSurname());
		user.setEmail(form.getEmail());
		user.setCompany(company);
		return userRepository.save(user);
	}

	@Override
	public Company updateCompany(CompaniesUploadForm form, String companyId) {
		Company company = companyRepository.findOne(companyId);
		company.setAddress(form.getAddress());
		company.setEmail(form.getEmail());
		company.setPhone(form.getPhone());
		return companyRepository.save(company);
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
