package com.phobod.study.vcp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.form.UploadForm;
import com.phobod.study.vcp.security.SecurityUtils;
import com.phobod.study.vcp.service.UserService;

@RestController
@RequestMapping("/my-account")
public class UserController {
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void uploadVideo(UploadForm uploadForm ){
		User currentUser = SecurityUtils.getCurrentUser();
		userService.uploadVideo(currentUser, uploadForm);
	} 

}
