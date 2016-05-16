package com.phobod.study.vcp.service.impl;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.phobod.study.vcp.Constants.Role;
import com.phobod.study.vcp.domain.Company;
import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.domain.VideoStatistics;
import com.phobod.study.vcp.exception.CantProcessStatisticsException;
import com.phobod.study.vcp.repository.statistics.VideoStatisticsRepository;
import com.phobod.study.vcp.service.VideoStatisticsService;

@RunWith(MockitoJUnitRunner.class)
public class AsyncVideoStatisticsServiceTest{
	@InjectMocks
	private VideoStatisticsService videoStatisticsService = new AsyncVideoStatisticsService();

	@Mock
	private VideoStatisticsRepository videoStatisticsRepository;

	private User user;
	private Video video;
	private String ip;

	@Before
	public void setUp() throws Exception {
		user = new User("TestUserName", "TestUserSurname", "TestUserLogin", "1111", "test.user@email.com", new Company(), Role.USER, "http://www.gravatar.com/avatar/00000000000000000000000000000000?d=mm");
		user.setId("userId");
		video = new Video("title", "description", "thumbnailUrl", "videoUrl");
		video.setId("videoId");
		video.setOwner(user);
		ip = "192.168.0.1";
	}

	@Test(timeout = 200)
	public void testSaveVideoViewStatisticsForExistVideo() throws Exception {		
		VideoStatistics videoStatistics = new VideoStatistics(video.getId(),video.getTitle(),100);
		when(videoStatisticsRepository.findOne(video.getId())).thenReturn(videoStatistics);
		videoStatisticsService.saveVideoViewStatistics(video, user, ip);
		Thread.sleep(100);
		verify(videoStatisticsRepository).findOne(video.getId());
		verify(videoStatisticsRepository).save(argThat(new VideoStatisticsArgumentMatcher(101)));;
	}

	@Test(timeout = 200)
	public void testSaveVideoViewStatisticsForNewVideo() throws Exception {		
		when(videoStatisticsRepository.findOne(video.getId())).thenReturn(null);
		videoStatisticsService.saveVideoViewStatistics(video, user, ip);
		Thread.sleep(100);
		verify(videoStatisticsRepository).findOne(video.getId());
		verify(videoStatisticsRepository).save(argThat(new VideoStatisticsArgumentMatcher(1)));
	}
	
	@Test(expected = CantProcessStatisticsException.class)
	public void testListVideoStatisticsWithException() {
		when(videoStatisticsRepository.findByDate(anyString())).thenThrow(new IllegalArgumentException("Exception"));
		videoStatisticsService.listVideoStatistics();
	}

	@Test
	public void testListVideoStatisticsSuccess() {
		videoStatisticsService.listVideoStatistics();
		verify(videoStatisticsRepository).findByDate(anyString());
	}

	private class VideoStatisticsArgumentMatcher extends ArgumentMatcher<VideoStatistics>{
		private long viewCount; 

		public VideoStatisticsArgumentMatcher(long viewCount) {
			super();
			this.viewCount = viewCount;
		}

		@Override
		public boolean matches(Object argument) {
			if (argument instanceof VideoStatistics) {
				VideoStatistics statistics = (VideoStatistics)argument;
				if (statistics.getVideoId() == video.getId() && statistics.getTitle() == video.getTitle() && statistics.getUserIdSet().contains(user.getId()) 
						&& statistics.getIpAddressSet().contains(ip) && statistics.getViewCount() == viewCount) {
					return true;
				}
			}
			return false;
		}
	}

}
