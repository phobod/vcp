package com.phobod.study.vcp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.VideoUploadForm;
import com.phobod.study.vcp.security.CurrentUser;
import com.phobod.study.vcp.security.SecurityUtils;
import com.phobod.study.vcp.service.CommonService;
import com.phobod.study.vcp.service.UserService;

@RestController
@RequestMapping("/my-account")
public class UserController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private CommonService commonService;
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void uploadVideo(VideoUploadForm uploadForm ){
		User currentUser = SecurityUtils.getCurrentUser();
		userService.uploadVideo(currentUser, uploadForm);
	} 
	
	@RequestMapping(value = "/video", method = RequestMethod.GET)
	public @ResponseBody PagedResources<Resource<Video>> listVideos(@AuthenticationPrincipal CurrentUser currentUser, Pageable pageable, PagedResourcesAssembler<Video> assembler) {
		Page<Video> videos = userService.listVideosByUser(pageable, currentUser.getUser().getId());
		return assembler.toResource(videos);
	}

	@RequestMapping(value = "/video/{videoId}", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void updateVideo(@PathVariable String videoId, VideoUploadForm uploadForm) {
		Video video = commonService.findVideoById(videoId);
		if (video.getOwner().equals(SecurityUtils.getCurrentUser())) {
			userService.updateVideo(videoId, uploadForm);
		}
	}

	@RequestMapping(value = "/video/{videoId}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void deleteVideo(@PathVariable String videoId) {
		Video video = commonService.findVideoById(videoId);
		if (video.getOwner().equals(SecurityUtils.getCurrentUser())) {
			userService.deleteVideo(videoId);
		}
	}


}
