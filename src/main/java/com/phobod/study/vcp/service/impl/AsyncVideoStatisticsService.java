package com.phobod.study.vcp.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.service.VideoStatisticsService;

@Service
public class AsyncVideoStatisticsService implements VideoStatisticsService {
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@PreDestroy
	private void preDestroy() {
		executorService.shutdown();
	}

	@Override
	public void saveVideoViewStatistics(String videoId, User user, String userIP) {
		executorService.submit(new StatisticsItem(videoId, user.getId(),userIP,getCurrentTime()));
	}

	public static String getCurrentTime() {
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	private class StatisticsItem implements Runnable {
		private String videoId;
		private String userId;
		private String userIP;
		private String date;

		private StatisticsItem(String videoId, String userId, String userIP, String date) {
			super();
			this.videoId = videoId;
			this.userId = userId;
			this.userIP = userIP;
			this.date = date;
		}

		@Override
		public void run() {
			incrementVideoViewCount(videoId, date);
			addUserToVideoViewStatistics(videoId, date, userId);
			addIPAddressToVideoViewStatistics(videoId, date, userIP);
		}

		private void incrementVideoViewCount(String videoId, String date) {
			String key = "view:" + date;
			redisTemplate.opsForZSet().incrementScore(key, videoId, 1);
		}

		private void addUserToVideoViewStatistics(String videoId, String date, String userId) {
			String key = "user:" + videoId + ":" + date;
			redisTemplate.opsForSet().add(key, userId);
		}

		private void addIPAddressToVideoViewStatistics(String videoId, String date, String userIP) {
			String key = "ip:" + videoId + ":" + date;
			redisTemplate.opsForSet().add(key, userIP);
		}

	}
}
