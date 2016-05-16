package com.phobod.study.vcp.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.service.VideoService;

@RunWith(MockitoJUnitRunner.class)
public class FileStorageVideoServiceTest {
	@InjectMocks
	private VideoService videoService = new FileStorageVideoService();

	private String mediaDir;
	private Path testVideoFilePath;
	private Path testIncorrectVideoFilePath;

	@Before
	public void setUp() throws Exception {
		testVideoFilePath = Paths.get("src/main/webapp/static/video/video-stub.mp4");
		testIncorrectVideoFilePath = Paths.get("src/main/webapp/static/video/not-exist.mp4");
		mediaDir="D:/Developing/workspace/vcp/src/main/webapp/media";
		setUpPrivateField("mediaDir",mediaDir);
	}
	
	private void setUpPrivateField(String name, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field fromEmailField = videoService.getClass().getDeclaredField(name);
		fromEmailField.setAccessible(true);
		fromEmailField.set(videoService,value);
	}	
	
	@Test(expected = CantProcessMediaContentException.class)
	public final void testSaveVideoWithNull() {
		videoService.saveVideo(null);
	}

	@Test(expected = CantProcessMediaContentException.class)
	public final void testSaveVideoWithIncorrectPath() {
		videoService.saveVideo(testIncorrectVideoFilePath);
	}

	@Test
	public final void testSaveVideoSuccess() {
		String videoFileName = videoService.saveVideo(testVideoFilePath);
		assertNotNull(videoFileName);
		File file = new File("D:/Developing/workspace/vcp/src/main/webapp/", videoFileName);
		assertTrue(file.exists());
		if (file.exists()) {
			file.delete();
		}
	}
}
