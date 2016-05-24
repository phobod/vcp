package com.phobod.study.vcp.service.impl;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.exception.ValidationException;
import com.phobod.study.vcp.repository.storage.CompanyRepository;
import com.phobod.study.vcp.repository.storage.UserRepository;
import com.phobod.study.vcp.service.AdminService;
import com.phobod.study.vcp.service.UserService;

@Service
public class AdminServiceImpl implements AdminService {

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
	public User saveUser(User user) throws ValidationException {
		try {
			return saveUserInternal(user);
		} catch (DuplicateKeyException e) {
			throw new ValidationException("Can't save user. User with the same parameter already exists: " + e.getMessage(), e);
		}
	}

	private User saveUserInternal(User user) {
		if (!checkEmailWithRegExp(user.getEmail()) || user.getPassword() == null || (!checkPasswordWithRegExp(user.getPassword()) & user.getId() == null)) {
			throw new ValidationException("Can't save user. The data entered is not correct!");
		}
		if (user.getId() == null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		} else {
			User existUser = userRepository.findOne(user.getId());
			if (!existUser.getPassword().equals(user.getPassword())) {
				throw new ValidationException("Can't save user. The password for the existing user doesn't match!");
			}
		}
		return userRepository.save(user);
	}

	@Override
	public Company saveCompany(Company company) throws ValidationException {
		try {
			return saveCompanyInternal(company);
		} catch (DuplicateKeyException e) {
			throw new ValidationException("Can't save company. Company with the same parameter already exists: " + e.getMessage(), e);
		}
	}

	private Company saveCompanyInternal(Company company) {
		if (!checkEmailWithRegExp(company.getEmail()) || (!checkPhoneNumberWithRegExp(company.getPhone()))) {
			throw new ValidationException("Can't save company. The data entered is not correct!");
		}
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

	private boolean checkPasswordWithRegExp(String password) {
		Pattern p = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,12}$");
		return p.matcher(password).matches();
	}

	private boolean checkEmailWithRegExp(String password) {
		Pattern p = Pattern.compile("^\\w+[\\w-\\.]*\\@\\w+((-\\w+)|(\\w*))\\.[a-z]{2,3}$");
		return p.matcher(password).matches();
	}

	private boolean checkPhoneNumberWithRegExp(String password) {
		Pattern p = Pattern.compile("(\\+)?(\\()?(\\d+){1,4}(\\))?(-)?(\\d+){1,3}?(-)?(\\d+){1,4}?(-)?(\\d+){1,4}?(-)?(\\d+){1,4}");
		return p.matcher(password).matches();
	}
}
