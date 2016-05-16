package com.phobod.study.vcp.component;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.form.VideoUploadForm;

@RunWith(MockitoJUnitRunner.class)
public class UploadVideoTempStorageTest {
	@InjectMocks
	private UploadVideoTempStorage uploadVideoTempStorage = new UploadVideoTempStorage();
	
	@Mock
	private ProceedingJoinPoint proceedingJoinPoint;
	
	@Mock
	private ThreadLocal<Path> tempUploadedVideoPathStorage;
	
	@Mock
	private MultipartFile file;

	private VideoUploadForm[] videoUploadForm;
	
	@Before
	public void setUp() throws Exception {
		videoUploadForm = new VideoUploadForm[]{new VideoUploadForm("title","description",file)};
	}
	
	@Test(expected = CantProcessMediaContentException.class)
	public final void testAdviceWithException() throws Throwable {
		when(proceedingJoinPoint.getArgs()).thenReturn(videoUploadForm);
		when(proceedingJoinPoint.proceed()).thenThrow(new IOException());
		uploadVideoTempStorage.advice(proceedingJoinPoint);
	}		

	@Test
	public final void testAdviceSuccess() throws Throwable {
		when(proceedingJoinPoint.getArgs()).thenReturn(videoUploadForm);
		uploadVideoTempStorage.advice(proceedingJoinPoint);
		verify(proceedingJoinPoint).proceed();
	}		

}
