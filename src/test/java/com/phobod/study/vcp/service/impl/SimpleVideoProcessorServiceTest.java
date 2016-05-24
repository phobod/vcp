package com.phobod.study.vcp.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
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
import com.phobod.study.vcp.service.ImageProcessorService;
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
	private ImageProcessorService imageProcessorService;

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
		setUpPrivateField("mediaDir", TestUtils.getProperties().getProperty("media.dir"));
	}

	private void setUpPrivateField(String name, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field fromEmailField = videoProcessorService.getClass().getDeclaredField(name);
		fromEmailField.setAccessible(true);
		fromEmailField.set(videoProcessorService, value);
	}

	@Test
	public final void testProcessVideo() {
		when(uploadVideoTempStorage.getTempUploadedVideoPath(TestUtils.getVideoUploadForm())).thenReturn(tempUploadedVideoPath);
		when(videoService.saveVideo(tempUploadedVideoPath)).thenReturn(videoUrl);
		when(thumbnailService.createThumbnail(tempUploadedVideoPath)).thenReturn(thumbnailImageData);
		when(imageService.saveImageData(thumbnailImageData)).thenReturn(thumbnailImageUrl);
		Video video = videoProcessorService.processVideo(TestUtils.getVideoUploadForm());
		assertEquals(thumbnailImageUrl, video.getThumbnailUrl());
		assertEquals(videoUrl, video.getVideoUrl());
		verify(imageProcessorService, times(2)).resizeImageFile(anyString(), anyString(), anyString());
		verify(imageProcessorService, times(3)).optimizeJpegImageFile(anyString());
	}

}
