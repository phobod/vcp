package com.phobod.study.vcp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.form.CompaniesUploadForm;
import com.phobod.study.vcp.form.UsersUploadForm;
import com.phobod.study.vcp.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	
	@RequestMapping(value = "/account", method = RequestMethod.GET)
	public @ResponseBody PagedResources<Resource<User>> listAllUser(Pageable pageable, PagedResourcesAssembler<User> assembler) {
		Page<User> users = adminService.listAllUsers(pageable);
		return assembler.toResource(users);
	}

	@RequestMapping(value = "/company", method = RequestMethod.GET)
	public @ResponseBody PagedResources<Resource<Company>> listAllCompaniesByPage(Pageable pageable, PagedResourcesAssembler<Company> assembler) {
		Page<Company> users = adminService.listAllCompanies(pageable);
		return assembler.toResource(users);
	}

	@RequestMapping(value = "/company/all", method = RequestMethod.GET)
	public @ResponseBody List<Company> listAllCompanies() {
		return adminService.listAllCompanies();
	}

	@RequestMapping(value = "/account", method = RequestMethod.POST)
	public @ResponseBody User addUser(UsersUploadForm uploadForm) {
		return adminService.addUser(uploadForm);
	}

	@RequestMapping(value = "/company", method = RequestMethod.POST)
	public @ResponseBody Company addCompany(CompaniesUploadForm uploadForm) {
		return adminService.addCompany(uploadForm);
	}

	@RequestMapping(value = "/account/{userId}", method = RequestMethod.POST)
	public @ResponseBody User updateUser(@PathVariable String userId, UsersUploadForm uploadForm) {
		return adminService.updateUser(uploadForm, userId);
	}

	@RequestMapping(value = "/company/{companyId}", method = RequestMethod.POST)
	public @ResponseBody Company updateCompany(@PathVariable String companyId, CompaniesUploadForm uploadForm) {
		return adminService.updateCompany(uploadForm, companyId);
	}

	@RequestMapping(value = "/account/{userId}", method = RequestMethod.DELETE)
	public void deleteUser(@PathVariable String userId) {
		adminService.deleteUser(userId);
	}

	@RequestMapping(value = "/company/{companyId}", method = RequestMethod.DELETE)
	public void deleteCompany(@PathVariable String companyId) {
		adminService.deleteCompany(companyId);
	}

}
