package com.phobod.study.vcp.service.impl;

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

import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.VideoUploadForm;
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
	
	private VideoUploadForm uploadForm;
	private Video processedVideo;

	@Before
	public void setUp() throws Exception {
		processedVideo = new Video("testThumbnailUrl", "testVideoUrl");
		uploadForm = new VideoUploadForm();
	}
	
	@Test
	public final void testProcessVideo() throws Exception {
		videoProcessorService.processVideo(uploadForm);
		when(simpleVideoProcessorService.processVideo(uploadForm)).thenReturn(processedVideo);
		Thread.sleep(100);
		verify(simpleVideoProcessorService).processVideo(uploadForm);
		verify(videoRepository).save(argThat(new VideoArgumentMatcher()));
	}
	
	private class VideoArgumentMatcher extends ArgumentMatcher<Video>{
		@Override
		public boolean matches(Object argument) {
			if (argument instanceof Video) {
				Video video = (Video)argument;
				if (processedVideo.getThumbnailUrl().equals(video.getThumbnailUrl()) && processedVideo.getVideoUrl().equals(video.getVideoUrl())) {
					return true;
				}
			}
			return false;
		}
	}
}
