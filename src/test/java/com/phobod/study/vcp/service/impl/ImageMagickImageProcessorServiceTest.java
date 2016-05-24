package com.phobod.study.vcp.service.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.phobod.study.vcp.component.TestUtils;
import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.service.ImageProcessorService;

@RunWith(MockitoJUnitRunner.class)
public class ImageMagickImageProcessorServiceTest {
	private ImageProcessorService imageProcessorService = new ImageMagickImageProcessorService();

	private String sourceFile;
	private String targerFile;

	@Before
	public void setUp() throws Exception {
		setUpPrivateField("imageMagicConvert", TestUtils.getProperties().getProperty("imageMagicConvert"));
		setUpPrivateField("jpegtran", TestUtils.getProperties().getProperty("jpegtran"));
		sourceFile = System.getProperty("user.dir") + "/src/test/resources/img/image-stub.jpg";
		targerFile = System.getProperty("user.dir") + "/src/test/resources/img/test-image-stub.jpg";
	}

	private void setUpPrivateField(String name, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field fromEmailField = imageProcessorService.getClass().getDeclaredField(name);
		fromEmailField.setAccessible(true);
		fromEmailField.set(imageProcessorService, value);
	}

	@Test(expected = CantProcessMediaContentException.class)
	public final void testResizeImageFileWithNullSource() {
		imageProcessorService.resizeImageFile(null, targerFile, "100x100");
	}

	@Test(expected = CantProcessMediaContentException.class)
	public final void testResizeImageFileWithNullTagert() {
		imageProcessorService.resizeImageFile(sourceFile, null, "100x100");
	}

	@Test(expected = CantProcessMediaContentException.class)
	public final void testResizeImageFileWithNullSize() {
		imageProcessorService.resizeImageFile(sourceFile, targerFile, null);
	}

	@Test
	public final void testResizeImageFileSuccess() {
		imageProcessorService.resizeImageFile(sourceFile, targerFile, "100x100");
		File file = new File(targerFile);
		assertTrue(file.exists());
		if (file.exists()) {
			file.delete();
		}

	}

	@Test(expected = CantProcessMediaContentException.class)
	public final void testOptimizeJpegImageFileWithNull() {
		imageProcessorService.optimizeJpegImageFile(null);
	}

	@Test
	public final void testOptimizeJpegImageFileSuccess() {
		imageProcessorService.optimizeJpegImageFile(sourceFile);
	}

}
