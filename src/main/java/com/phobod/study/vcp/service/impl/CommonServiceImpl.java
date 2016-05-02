package com.phobod.study.vcp.service.impl;

import java.util.List;

import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.repository.search.VideoSearchRepository;
import com.phobod.study.vcp.repository.storage.VideoRepository;
import com.phobod.study.vcp.service.CommonService;

@Service
public class CommonServiceImpl implements CommonService {
	
	@Autowired
	private VideoRepository videoRepository;
	
	@Autowired
	private VideoSearchRepository videoSearchRepository;

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

	@Override
	public Page<Video> listVideosBySearchQuery(String query, Pageable pageable) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withQuery(QueryBuilders.multiMatchQuery(query)
						.field("title")
						.field("owner.company.name")
						.field("owner.name")
						.field("owner.surname")
						.type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
						.fuzziness(Fuzziness.TWO)
						.operator(Operator.OR))
				.withSort(SortBuilders.fieldSort("views").order(SortOrder.DESC))
				.build();
		searchQuery.setPageable(pageable);
		return videoSearchRepository.search(searchQuery);
	}
	
}
