package com.phobod.study.vcp.component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.form.VideoUploadForm;

@Aspect
@Component
public class UploadVideoTempStorage {
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadVideoTempStorage.class);
	private Map<VideoUploadForm, Path> tempUploadedVideoPathStorage = new HashMap<>();
	
	public Path getTempUploadedVideoPath(VideoUploadForm videoUploadForm) {
		return tempUploadedVideoPathStorage.get(videoUploadForm);
	}
	
	@Before("execution(* com.phobod.study.vcp.service.impl.AsyncVideoProcessorService.processVideo(..)) && args(videoUploadForm,..)")
	public void copyDataToTempStorage(JoinPoint joinPoint, VideoUploadForm videoUploadForm) throws Throwable{
		Path tempUploadedVideoPath = null;
		try {
			tempUploadedVideoPath = Files.createTempFile("upload", ".video");
			videoUploadForm.getFile().transferTo(tempUploadedVideoPath.toFile());
			tempUploadedVideoPathStorage.put(videoUploadForm, tempUploadedVideoPath);
		} catch (IOException e) {
			throw new CantProcessMediaContentException("Can't save video content to temp file: " + e.getMessage(), e);
		}
	}
	
	@After("execution(* com.phobod.study.vcp.service.impl.SimpleVideoProcessorService.processVideo(..)) && args(videoUploadForm,..)")
	public void releaseTempStorage(JoinPoint joinPoint, VideoUploadForm videoUploadForm){
		Path tempUploadedVideoPath = getTempUploadedVideoPath(videoUploadForm);
		tempUploadedVideoPathStorage.remove(videoUploadForm);
		if (tempUploadedVideoPath != null) {
			try {
				Files.deleteIfExists(tempUploadedVideoPath);
			} catch (Exception e) {
				LOGGER.warn("Can't remove temp file: " + tempUploadedVideoPath, e);
			}
		}
	}
	
}
