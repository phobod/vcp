package com.phobod.study.vcp.component;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.aspectj.lang.JoinPoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import com.phobod.study.vcp.exception.CantProcessMediaContentException;

@RunWith(MockitoJUnitRunner.class)
public class UploadVideoTempStorageTest {
	@InjectMocks
	private UploadVideoTempStorage uploadVideoTempStorage = new UploadVideoTempStorage();

	@Mock
	private JoinPoint joinPoint;

	@Mock
	private ThreadLocal<Path> tempUploadedVideoPathStorage;

	@Mock
	private MultipartFile file;

	@Test(expected = CantProcessMediaContentException.class)
	public final void testCopyDataToTempStorageWithException() throws Throwable {
		doThrow(new IOException()).when(file).transferTo(any(File.class));
		uploadVideoTempStorage.copyDataToTempStorage(joinPoint, TestUtils.getVideoUploadFormWithFile(file));
	}

}
