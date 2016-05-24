package com.phobod.study.vcp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.RecoveryForm;
import com.phobod.study.vcp.security.CurrentUser;
import com.phobod.study.vcp.service.CommonService;
import com.phobod.study.vcp.service.UserService;

@RestController
public class CommonController {
	@Autowired
	private CommonService commonService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/video/popular", method = RequestMethod.GET)
	public List<Video> listPopularVideos() {
		return commonService.listPopularVideos();
	}

	@RequestMapping(value = "/video/all", method = RequestMethod.GET)
	public Page<Video> listAllVideos(Pageable pageable) {
		return commonService.listAllVideos(pageable);
	}

	@RequestMapping(value = "/video/{videoId}", method = RequestMethod.GET)
	public Video findVideoById(@AuthenticationPrincipal CurrentUser currentUser, @PathVariable String videoId, HttpServletRequest request) {
		return commonService.findVideoById(videoId, currentUser.getUser(), request.getRemoteAddr());
	}

	@RequestMapping(value = "/video/search", method = RequestMethod.GET)
	public Page<Video> listVideosBySearchQuery(@RequestParam("query") String query, Pageable pageable) {
		return commonService.listVideosBySearchQuery(query, pageable);
	}

	@RequestMapping(value = "/user/{userId}/video", method = RequestMethod.GET)
	public Page<Video> listVideosByUser(@PathVariable String userId, Pageable pageable) {
		return userService.listVideosByUser(pageable, userId);
	}

	@RequestMapping(value = "/user/{userId}/video/{excludedVideoId}", method = RequestMethod.GET)
	public Page<Video> listVideosByUserExcludeOne(@PathVariable String userId, @PathVariable String excludedVideoId, Pageable pageable) {
		return userService.listVideosByUserExcludeOne(pageable, excludedVideoId, userId);
	}

	@RequestMapping(value = "/recovery/{login}", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void sendRestoreEmail(@PathVariable String login) {
		commonService.sendRestoreEmail(login);
	}

	@RequestMapping(value = "/recovery/password", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void restorePassword(@RequestBody RecoveryForm form) {
		commonService.restorePassword(form);
	}

}
