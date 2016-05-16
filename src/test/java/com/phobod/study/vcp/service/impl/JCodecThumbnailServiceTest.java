package com.phobod.study.vcp.service.impl;

import static org.junit.Assert.assertNotNull;

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
public class JCodecThumbnailServiceTest {
	@InjectMocks
	private ThumbnailService thumbnailService = new JCodecThumbnailService();

	private Path testVideoFilePath;
	private Path testIncorrectVideoFilePath;

	@Before
	public void setUp() throws Exception {
		testVideoFilePath = Paths.get("src/main/webapp/static/video/video-stub.mp4");
		testIncorrectVideoFilePath = Paths.get("src/main/webapp/static/video/incorrect-video-stub.mp4");
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
