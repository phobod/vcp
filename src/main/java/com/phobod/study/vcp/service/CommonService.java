package com.phobod.study.vcp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.phobod.study.vcp.domain.Video;

public interface CommonService {
	List<Video> listPopularVideos();
	Page<Video> listAllVideos(Pageable pageable);
	Video findVideoById(String id);
	Page<Video> listVideosByUser(Pageable pageable, String excludedVideoId, String userId);
}
