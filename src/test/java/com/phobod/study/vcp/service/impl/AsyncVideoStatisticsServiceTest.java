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

import com.phobod.study.vcp.component.TestUtils;
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

	private String userId;
	private String videoId;
	private String ip;

	@Before
	public void setUp() throws Exception {
		userId = "userId";
		videoId = "videoId";
		ip = "192.168.0.1";
	}

	@Test(timeout = 300)
	public void testSaveVideoViewStatisticsForExistVideo() throws Exception {		
		VideoStatistics videoStatistics = new VideoStatistics(videoId,TestUtils.getTestVideoWithId(videoId).getTitle(),100);
		when(videoStatisticsRepository.findOne(videoId)).thenReturn(videoStatistics);
		videoStatisticsService.saveVideoViewStatistics(TestUtils.getTestVideoWithId(videoId), TestUtils.getTestUserWithId(userId), ip);
		Thread.sleep(100);
		verify(videoStatisticsRepository).findOne(videoId);
		verify(videoStatisticsRepository).save(argThat(new VideoStatisticsArgumentMatcher(101)));;
	}

	@Test(timeout = 200)
	public void testSaveVideoViewStatisticsForNewVideo() throws Exception {		
		when(videoStatisticsRepository.findOne(videoId)).thenReturn(null);
		videoStatisticsService.saveVideoViewStatistics(TestUtils.getTestVideoWithId(videoId), TestUtils.getTestUserWithId(userId), ip);
		Thread.sleep(100);
		verify(videoStatisticsRepository).findOne(videoId);
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
				if (videoId.equals(statistics.getVideoId()) && TestUtils.getTestVideoWithId(videoId).getTitle().equals(statistics.getTitle()) && statistics.getUserIdSet().contains(userId) 
						&& statistics.getIpAddressSet().contains(ip) && statistics.getViewCount() == viewCount) {
					return true;
				}
			}
			return false;
		}
	}

}
