package com.phobod.study.vcp.service;

import javax.annotation.Nonnull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.User;

public interface AdminService {
	@Nonnull Page<User> listAllUsers(@Nonnull Pageable pageable);
	@Nonnull Page<Company> listAllCompanies(@Nonnull Pageable pageable);
	@Nonnull User saveUser(@Nonnull User user);
	@Nonnull Company saveCompany(@Nonnull Company company);
	void deleteUser(@Nonnull String userId);
	void deleteCompany(@Nonnull String companyId);
}
