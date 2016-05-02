package com.phobod.study.vcp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.phobod.study.vcp.domain.User;
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

}
