package com.phobod.study.vcp.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.form.CompaniesUploadForm;
import com.phobod.study.vcp.form.UsersUploadForm;

public interface AdminService {
	@Nonnull Page<User> listAllUsers(@Nonnull Pageable pageable);
	@Nonnull Page<Company> listAllCompanies(@Nonnull Pageable pageable);
	@Nonnull List<Company> listAllCompanies();
	@Nonnull User addUser(@Nonnull UsersUploadForm form);
	@Nonnull Company addCompany(@Nonnull CompaniesUploadForm form);
	@Nonnull User updateUser(@Nonnull UsersUploadForm form, @Nonnull String userId);
	@Nonnull Company updateCompany(@Nonnull CompaniesUploadForm form, @Nonnull String companyId);
	void deleteUser(@Nonnull String userId);
	void deleteCompany(@Nonnull String companyId);
}
