package com.phobod.study.vcp.service.impl;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.phobod.study.vcp.component.TestUtils;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.repository.storage.VideoRepository;
import com.phobod.study.vcp.service.VideoProcessorService;

@RunWith(MockitoJUnitRunner.class)
public class AsyncVideoProcessorServiceTest {
	@InjectMocks
	private VideoProcessorService videoProcessorService = new AsyncVideoProcessorService();

	@Mock
	private VideoRepository videoRepository;

	@Mock
	private VideoProcessorService simpleVideoProcessorService;

	@Test
	public final void testProcessVideo() throws Exception {
		when(simpleVideoProcessorService.processVideo(TestUtils.getVideoUploadForm())).thenReturn(TestUtils.getTestVideoWithoutId());
		videoProcessorService.processVideo(TestUtils.getVideoUploadForm());
		Thread.sleep(300);
		verify(simpleVideoProcessorService).processVideo(TestUtils.getVideoUploadForm());
		verify(videoRepository).save(argThat(new VideoArgumentMatcher()));
	}

	private class VideoArgumentMatcher extends ArgumentMatcher<Video> {
		@Override
		public boolean matches(Object argument) {
			if (argument instanceof Video) {
				Video video = (Video) argument;
				if (TestUtils.getTestVideoWithoutId().getThumbnailUrl().equals(video.getThumbnailUrl())
						&& TestUtils.getTestVideoWithoutId().getVideoUrl().equals(video.getVideoUrl())) {
					return true;
				}
			}
			return false;
		}
	}
}
