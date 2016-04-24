package com.phobod.study.vcp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
	public List<Video> listPopularVideos() {
		return videoRepository.findTop3ByOrderByViewsDesc();
	}
	
	@Override
	public Page<Video> listAllVideos(Pageable pageable) {
		return videoRepository.findAll(pageable);
	}

	@Override
	public Video findVideoById(String id) {
		return videoRepository.findOne(id);
	}
	
}
