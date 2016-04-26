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
import org.springframework.web.bind.annotation.RequestParam;
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

	@RequestMapping(value = "/video/all", method = RequestMethod.GET)
	public @ResponseBody PagedResources<Resource<Video>> listAllVideos(Pageable pageable, PagedResourcesAssembler<Video> assembler) {
		Page<Video> videos = commonService.listAllVideos(pageable);
		return assembler.toResource(videos);
	}
	
	@RequestMapping(value = "/video/{videoId}", method = RequestMethod.GET)
	public @ResponseBody Video findVideoById(@PathVariable String videoId) {
		Video video = commonService.findVideoById(videoId);
		return video;
	}
	
	@RequestMapping(value = "/video/search", method = RequestMethod.GET)
	public @ResponseBody PagedResources<Resource<Video>> findVideos(@RequestParam("query") String query, Pageable pageable, PagedResourcesAssembler<Video> assembler) {
		Page<Video> videos = commonService.listVideosBySearchQuery(query, pageable);
		return assembler.toResource(videos);
	}	

}
