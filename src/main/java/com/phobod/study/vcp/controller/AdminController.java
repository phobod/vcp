package com.phobod.study.vcp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.VideoStatistics;
import com.phobod.study.vcp.form.AvatarUrlGenerationForm;
import com.phobod.study.vcp.service.AdminService;
import com.phobod.study.vcp.service.AvatarService;
import com.phobod.study.vcp.service.VideoStatisticsService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private AvatarService avatarService;
	
	@Autowired
	private VideoStatisticsService videoStatisticsService;
	
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

	@RequestMapping(value = "/account", method = RequestMethod.POST)
	public @ResponseBody User saveUser(@RequestBody User user) {
		return adminService.saveUser(user);
	}

	@RequestMapping(value = "/company", method = RequestMethod.POST)
	public @ResponseBody Company saveCompany(@RequestBody Company company) {
		return adminService.saveCompany(company);
	}

	@RequestMapping(value = "/account/{userId}", method = RequestMethod.DELETE)
	public void deleteUser(@PathVariable String userId) {
		adminService.deleteUser(userId);
	}

	@RequestMapping(value = "/company/{companyId}", method = RequestMethod.DELETE)
	public void deleteCompany(@PathVariable String companyId) {
		adminService.deleteCompany(companyId);
	}

	@RequestMapping(value = "/emailhash", method = RequestMethod.POST)
	public @ResponseBody AvatarUrlGenerationForm getAvatarUrl(@RequestBody AvatarUrlGenerationForm form) {
		String url = avatarService.generateAvatarUrl(form.getEmail());
		form.setUrl(url);
		return form;
	}

	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public @ResponseBody List<VideoStatistics> listVideoStatistics() {
		List<VideoStatistics> videoStatistics = videoStatisticsService.listVideoStatistics();
		return  videoStatistics;
	}

}
