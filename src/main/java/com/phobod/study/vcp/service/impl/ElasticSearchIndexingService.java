package com.phobod.study.vcp.service.impl;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.repository.search.VideoSearchRepository;
import com.phobod.study.vcp.repository.storage.VideoRepository;

@Service
public class ElasticSearchIndexingService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateTestDataService.class);
	
	@Value("${index.all.during.startup}")
	private boolean indexAllDuringStartup;
	
	@Autowired 
	private ElasticsearchOperations elasticsearchOperations;
	
	@Autowired
	private VideoRepository videoRepository;
	
	@Autowired
	private VideoSearchRepository videoSearchRepository;
	
	@PostConstruct
	public void postConstruct(){
		if(indexAllDuringStartup) {
			LOGGER.info("Detected indexAllDuringStartup command");
			LOGGER.info("Clear old index");
			elasticsearchOperations.deleteIndex(Video.class);
			LOGGER.info("Start indexing for videos");
			for(Video v : videoRepository.findAll()){
				videoSearchRepository.save(v);
				LOGGER.info("Successful indexed video: "+v.getVideoUrl());
			}
			LOGGER.info("Finish indexing of videos");
		}
		else{
			LOGGER.info("indexAllDuringStartup is disabled");
		}
	}
}
