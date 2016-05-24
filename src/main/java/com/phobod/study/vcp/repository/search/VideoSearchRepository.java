package com.phobod.study.vcp.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.phobod.study.vcp.domain.Video;

public interface VideoSearchRepository extends ElasticsearchRepository<Video, String> {
	Long deleteByOwnerId(String userId);
}
