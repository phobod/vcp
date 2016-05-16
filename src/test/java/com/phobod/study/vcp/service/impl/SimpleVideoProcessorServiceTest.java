package com.phobod.study.vcp.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.phobod.study.vcp.component.TestUtils;
import com.phobod.study.vcp.component.UploadVideoTempStorage;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.service.ImageService;
import com.phobod.study.vcp.service.ThumbnailService;
import com.phobod.study.vcp.service.VideoProcessorService;
import com.phobod.study.vcp.service.VideoService;

@RunWith(MockitoJUnitRunner.class)
public class SimpleVideoProcessorServiceTest {
	@InjectMocks
	private VideoProcessorService videoProcessorService = new SimpleVideoProcessorService();
	
	@Mock
	private VideoService videoService;
	
	@Mock
	private ThumbnailService thumbnailService;
	
	@Mock
	private ImageService imageService;
	
	@Mock
	private UploadVideoTempStorage uploadVideoTempStorage;
	
	private Path tempUploadedVideoPath;
	private String videoUrl;
	private byte[] thumbnailImageData;
	private String thumbnailImageUrl;

	@Before
	public void setUp() throws Exception {
		tempUploadedVideoPath = Paths.get("tempUploadedVideoPath");
		videoUrl = "videoUrl";
		thumbnailImageData = new byte[1024];
		thumbnailImageUrl = "thumbnailImageUrl";
	}

	@Test
	public final void testProcessVideo() {
		when(uploadVideoTempStorage.getTempUploadedVideoPath()).thenReturn(tempUploadedVideoPath);
		when(videoService.saveVideo(tempUploadedVideoPath)).thenReturn(videoUrl);
		when(thumbnailService.createThumbnail(tempUploadedVideoPath)).thenReturn(thumbnailImageData);
		when(imageService.saveImageData(thumbnailImageData)).thenReturn(thumbnailImageUrl);
		Video video = videoProcessorService.processVideo(TestUtils.getVideoUploadForm());
		assertEquals(thumbnailImageUrl, video.getThumbnailUrl());
		assertEquals(videoUrl, video.getVideoUrl());
	}

}
