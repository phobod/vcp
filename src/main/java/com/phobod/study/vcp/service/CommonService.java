package com.phobod.study.vcp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.phobod.study.vcp.domain.Video;

public interface CommonService {
	Page<Video> listAllVideos(Pageable pageable);
	Video videoById(String id);
	Page<Video> listPopularVideos(Pageable pageable);
	Page<Video> videoListByUser(Pageable pageable, String userId);
}
