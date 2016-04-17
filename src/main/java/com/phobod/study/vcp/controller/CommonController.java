package com.phobod.study.vcp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.service.CommonService;

@RestController
public class CommonController {
	@Autowired
	private CommonService commonService;

	@RequestMapping(value = "/popular", method = RequestMethod.GET)
	public @ResponseBody Page<Video> listPopularVideos(@PageableDefault(size = 3) Pageable pageable) {
		Page<Video> videos = commonService.listPopularVideos(pageable);
		return videos;
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody Page<Video> listAllVideos(@PageableDefault(size = 20) Pageable pageable) {
		Page<Video> videos = commonService.listAllVideos(pageable);
		return videos;
	}

	@RequestMapping(value = "/currentvideo/{videoId}", method = RequestMethod.GET)
	public @ResponseBody Video videoById(@PathVariable String videoId) {
		Video videos = commonService.videoById(videoId);
		return videos;
	}

	@RequestMapping(value = "/videobyuser/{userId}", method = RequestMethod.GET)
	public @ResponseBody Page<Video> videoListByUser(@PageableDefault(size = 3) Pageable pageable, @PathVariable String userId) {
		Page<Video> videos = commonService.videoListByUser(pageable, userId);
		return videos;
	}
}
