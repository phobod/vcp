package com.phobod.study.vcp.repository.statistics;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.phobod.study.vcp.domain.VideoStatistics;

public interface VideoStatisticsRepository extends CrudRepository<VideoStatistics, String>{
	List<VideoStatistics> findByDate(String date);
}
