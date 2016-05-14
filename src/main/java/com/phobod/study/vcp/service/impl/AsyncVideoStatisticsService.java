package com.phobod.study.vcp.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.domain.VideoStatistics;
import com.phobod.study.vcp.exception.CantProcessStatisticsException;
import com.phobod.study.vcp.repository.statistics.VideoStatisticsRepository;
import com.phobod.study.vcp.service.VideoStatisticsService;

@Service
public class AsyncVideoStatisticsService implements VideoStatisticsService {
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	@Autowired
	private VideoStatisticsRepository videoStatisticsRepository;
	
	@PreDestroy
	private void preDestroy() {
		executorService.shutdown();
	}

	@Override
	public void saveVideoViewStatistics(Video video, User user, String userIP) {
		executorService.submit(new StatisticsItem(video.getId(), video.getTitle(), user.getId(), userIP, getCurrentTime()));
	}

	@Override
	public List<VideoStatistics> listVideoStatistics() throws CantProcessStatisticsException{
		String currentDate = getCurrentTime();
		try {
			return videoStatisticsRepository.findByDate(currentDate);
		} catch (Throwable e) {
			throw new CantProcessStatisticsException("Can't get statistics for date " + currentDate + ": " + e.getMessage(), e);
		}
	}
	
	private String getCurrentTime() {
		return LocalDate.now().toString();
	}
	
	private class StatisticsItem implements Runnable {
		private String videoId;
		private String title;
		private String userId;
		private String ipAddress;
		private String date;

		private StatisticsItem(String videoId, String title, String userId, String ipAddress, String date) {
			super();
			this.videoId = videoId;
			this.title = title;
			this.userId = userId;
			this.ipAddress = ipAddress;
			this.date = date;
		}

		@Override
		public void run() {
			VideoStatistics videoStatistics = videoStatisticsRepository.findOne(videoId);
			if (videoStatistics == null){
				videoStatistics = new VideoStatistics(videoId);
			}
			videoStatistics.setDate(date);
			videoStatistics.setTitle(title);
			videoStatistics.setViewCount(videoStatistics.getViewCount() + 1);
			videoStatistics.addUserId(userId);
			videoStatistics.addIpAddress(ipAddress);
			videoStatistics.setExpiration(getExpirationTime());		
			videoStatisticsRepository.save(videoStatistics);
		}
		
	    private long getExpirationTime(){
	        return ChronoUnit.SECONDS.between(LocalTime.now(),LocalTime.MAX);
	    }

		
	}

}
