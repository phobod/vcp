package com.phobod.study.vcp.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.phobod.study.vcp.domain.Video;

public interface CommonService {
	List<Video> listPopularVideos();
	Page<Video> listAllVideos(int pageNumber);
	Video findVideoById(String id);
	Page<Video> listVideosByUser(String userId, int pageNumber);
}
