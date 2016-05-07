package com.phobod.study.vcp.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.User;

public interface AdminService {
	@Nonnull Page<User> listAllUsers(@Nonnull Pageable pageable);
	@Nonnull Page<Company> listAllCompanies(@Nonnull Pageable pageable);
	@Nonnull User saveUser(@Nonnull String userJson, @Nullable MultipartFile avatar);
	@Nonnull Company saveCompany(@Nonnull Company company);
	void deleteUser(@Nonnull String userId);
	void deleteCompany(@Nonnull String companyId);
}
