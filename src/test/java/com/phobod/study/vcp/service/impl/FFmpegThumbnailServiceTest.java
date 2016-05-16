package com.phobod.study.vcp.service.impl;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.service.ThumbnailService;

@RunWith(MockitoJUnitRunner.class)
public class FFmpegThumbnailServiceTest {
	@InjectMocks
	private ThumbnailService thumbnailService = new FFmpegThumbnailService();
	
	private Path testVideoFilePath;
	private Path testIncorrectVideoFilePath;
	private String ffmpeg;
	private String ffprobe;
	
	@Before
	public void setUp() throws Exception {
		testVideoFilePath = Paths.get("src/main/webapp/static/video/video-stub.mp4");
		testIncorrectVideoFilePath = Paths.get("src/main/webapp/static/video/incorrect-video-stub.mp4");
		setUpthumbnailServiceFields();
	}

	private void setUpthumbnailServiceFields() throws NoSuchFieldException, IllegalAccessException {
		ffmpeg = "D:/ffmpeg/bin/ffmpeg.exe";
		setUpPrivateField("ffmpeg",ffmpeg);
		ffprobe = "D:/ffmpeg/bin/ffprobe.exe";
		setUpPrivateField("ffprobe",ffprobe);
	}

	private void setUpPrivateField(String name, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field fromEmailField = thumbnailService.getClass().getDeclaredField(name);
		fromEmailField.setAccessible(true);
		fromEmailField.set(thumbnailService,value);
	}
	
	@Test(expected = CantProcessMediaContentException.class)
	public final void testCreateThumbnailWithNull() {
		thumbnailService.createThumbnail(null);
	}

	@Test(expected = CantProcessMediaContentException.class)
	public final void testCreateThumbnailWithIncorrectFile() {
		thumbnailService.createThumbnail(testIncorrectVideoFilePath);
	}

	@Test(expected = CantProcessMediaContentException.class)
	public final void testCreateThumbnailWithIncorrectPath() {
		thumbnailService.createThumbnail(Paths.get("src/main/webapp/static/video/not-exist.mp4"));
	}

	@Test
	public final void testCreateThumbnailSuccess() {
		byte[] thumbnail = thumbnailService.createThumbnail(testVideoFilePath);
		assertNotNull(thumbnail);
	}

}
