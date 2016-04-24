package com.phobod.study.vcp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.UploadForm;
import com.phobod.study.vcp.security.SecurityUtils;
import com.phobod.study.vcp.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/{userId}/video", method = RequestMethod.GET)
	public @ResponseBody PagedResources<Resource<Video>> listVideosByUser(@PathVariable String userId,Pageable pageable, PagedResourcesAssembler<Video> assembler) {
		Page<Video> videos = userService.listVideosByUser(pageable, userId);
		return assembler.toResource(videos);
	}
	
	@RequestMapping(value = "/{userId}/video/{excludedVideoId}", method = RequestMethod.GET)
	public @ResponseBody PagedResources<Resource<Video>> listVideosByUserExcludeOne(@PathVariable String userId, @PathVariable String excludedVideoId,Pageable pageable, PagedResourcesAssembler<Video> assembler) {
		Page<Video> videos = userService.listVideosByUserExcludeOne(pageable, excludedVideoId, userId);
		return assembler.toResource(videos);
	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void uploadVideo(UploadForm uploadForm ){
		User currentUser = SecurityUtils.getCurrentUser();
		userService.uploadVideo(currentUser, uploadForm);
	} 

}
