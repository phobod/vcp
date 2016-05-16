package com.phobod.study.vcp.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.service.ImageService;

@RunWith(MockitoJUnitRunner.class)
public class FileStorageImageServiceTest {
	private ImageService imageService = new FileStorageImageService();
	
	@Before
	public void setUp() throws Exception {
		setUpPrivateField("mediaDir","D:/Developing/workspace/vcp/src/main/webapp/media");
	}

	private void setUpPrivateField(String name, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field fromEmailField = imageService.getClass().getDeclaredField(name);
		fromEmailField.setAccessible(true);
		fromEmailField.set(imageService,value);
	}
	
	@Test(expected = CantProcessMediaContentException.class)
	public final void testSaveImageDataWithNull() {
		imageService.saveImageData(null);
	}

	@Test
	public final void testSaveImageDataSuccess() {
		String ThumbnailFileName = imageService.saveImageData(new byte[1024]);
		assertNotNull(ThumbnailFileName);
		File file = new File("D:/Developing/workspace/vcp/src/main/webapp/", ThumbnailFileName);
		assertTrue(file.exists());
		if (file.exists()) {
			file.delete();
		}
	}

}
