package com.phobod.study.vcp.repository.storage;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.phobod.study.vcp.domain.Video;

public interface VideoRepository extends PagingAndSortingRepository<Video, String>{
	List<Video> findTop3ByOrderByViewsDesc();
	List<Video> findAllByOwnerId(String userId);
}
