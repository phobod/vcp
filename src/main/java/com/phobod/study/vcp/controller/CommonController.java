package com.phobod.study.vcp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

	@RequestMapping(value = "/video/popular", method = RequestMethod.GET)
	public @ResponseBody List<Video> listPopularVideos() {
		List<Video> videos = commonService.listPopularVideos();
		return  videos;
	}

	@RequestMapping(value = "/video/all/{pageNumber}", method = RequestMethod.GET)
	public @ResponseBody Page<Video> listAllVideos(@PathVariable int pageNumber) {
		Page<Video> videos = commonService.listAllVideos(pageNumber);
		return videos;
	}

	@RequestMapping(value = "/video/{videoId}", method = RequestMethod.GET)
	public @ResponseBody Video findVideoById(@PathVariable String videoId) {
		Video video = commonService.findVideoById(videoId);
		return video;
	}

	@RequestMapping(value = "/user/{userId}/video/{pageNumber}", method = RequestMethod.GET)
	public @ResponseBody Page<Video> listVideosByUser(@PathVariable String userId, @PathVariable int pageNumber) {
		Page<Video> videos = commonService.listVideosByUser(userId, pageNumber);
		return videos;
	}

}
