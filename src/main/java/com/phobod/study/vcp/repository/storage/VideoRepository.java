package com.phobod.study.vcp.repository.storage;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.phobod.study.vcp.domain.Video;

public interface VideoRepository extends PagingAndSortingRepository<Video, String> {
	List<Video> findTop3ByOrderByViewsDesc();

	Page<Video> findByOwnerIdOrderByViewsDesc(Pageable pageable, String userId);

	Page<Video> findByIdNotAndOwnerIdOrderByViewsDesc(Pageable pageable, String excludedVideoId, String userId);

	List<Video> findByOwnerId(String userId);

	Long deleteByOwnerId(String userId);
}
