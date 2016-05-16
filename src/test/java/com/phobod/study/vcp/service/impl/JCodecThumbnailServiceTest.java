package com.phobod.study.vcp.service.impl;

import static org.junit.Assert.assertNotNull;

import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.service.ThumbnailService;

@RunWith(MockitoJUnitRunner.class)
public class JCodecThumbnailServiceTest {
	private ThumbnailService thumbnailService = new JCodecThumbnailService();

	@Test(expected = CantProcessMediaContentException.class)
	public final void testCreateThumbnailWithNull() {
		thumbnailService.createThumbnail(null);
	}

	@Test(expected = CantProcessMediaContentException.class)
	public final void testCreateThumbnailWithIncorrectFile() {
		thumbnailService.createThumbnail(Paths.get("src/test/resources/video/incorrect-video-stub.mp4"));
	}

	@Test(expected = CantProcessMediaContentException.class)
	public final void testCreateThumbnailWithIncorrectPath() {
		thumbnailService.createThumbnail(Paths.get("src/test/resources/video/not-exist.mp4"));
	}

	@Test
	public final void testCreateThumbnailSuccess() {
		byte[] thumbnail = thumbnailService.createThumbnail(Paths.get("src/main/webapp/static/video/video-stub.mp4"));
		assertNotNull(thumbnail);
	}

}
