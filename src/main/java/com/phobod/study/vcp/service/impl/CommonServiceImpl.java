package com.phobod.study.vcp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.repository.storage.VideoRepository;
import com.phobod.study.vcp.service.CommonService;

@Service
public class CommonServiceImpl implements CommonService {
	
	@Autowired
	private VideoRepository videoRepository;

	@Override
	public Page<Video> listAllVideos(Pageable pageable) {
		Page<Video> page = videoRepository.findAll(pageable);
		return page;
	}

	@Override
	public Page<Video> listPopularVideos(Pageable pageable) {
		List<Video> list = videoRepository.findTop3ByOrderByViewsDesc();
		return new PageImpl<>(list, pageable, 3);
	}
	
	@Override
	public Video videoById(String id) {
		Video video = videoRepository.findOne(id);
		return video;
	}
	
	@Override
	public Page<Video> videoListByUser(Pageable pageable, String userId) {
		List<Video> list = videoRepository.findAllByOwnerId(userId);
		return new PageImpl<>(list, pageable, 10);
	}


}
