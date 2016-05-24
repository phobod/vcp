package com.phobod.study.vcp.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.phobod.study.vcp.component.TestUtils;
import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.service.VideoService;

@RunWith(MockitoJUnitRunner.class)
public class FileStorageVideoServiceTest {
	private VideoService videoService = new FileStorageVideoService();

	private String mediaDir;

	@Before
	public void setUp() throws Exception {
		mediaDir = TestUtils.getProperties().getProperty("media.dir");
		setUpPrivateField("mediaDir", mediaDir);
	}

	private void setUpPrivateField(String name, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field fromEmailField = videoService.getClass().getDeclaredField(name);
		fromEmailField.setAccessible(true);
		fromEmailField.set(videoService, value);
	}

	@Test(expected = CantProcessMediaContentException.class)
	public final void testSaveVideoWithNull() {
		videoService.saveVideo(null);
	}

	@Test(expected = CantProcessMediaContentException.class)
	public final void testSaveVideoWithIncorrectPath() {
		videoService.saveVideo(Paths.get("src/main/webapp/static/video/not-exist.mp4"));
	}

	@Test
	public final void testSaveVideoSuccess() {
		String videoFileName = videoService.saveVideo(Paths.get("src/main/webapp/static/video/video-stub.mp4"));
		assertNotNull(videoFileName);
		File file = new File(mediaDir.substring(0, mediaDir.lastIndexOf("/media")), videoFileName);
		assertTrue(file.exists());
		if (file.exists()) {
			file.delete();
		}
	}
}
