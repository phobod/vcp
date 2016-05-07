package com.phobod.study.vcp.component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.form.VideoUploadForm;

@Aspect
@Component
public class UploadVideoTempStorage {
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadVideoTempStorage.class);
	private final ThreadLocal<Path> tempUploadedVideoPathStorage = new ThreadLocal<>();
	
	public Path getTempUploadedVideoPath() {
		return tempUploadedVideoPathStorage.get();
	}
	
	@Around("execution(* com.phobod.study.vcp.service.impl.SimpleVideoProcessorService.processVideo(..))")
	public Object advice(ProceedingJoinPoint pjp) throws Throwable {
		VideoUploadForm form = (VideoUploadForm) pjp.getArgs()[0];
		Path tempUploadedVideoPath = null;
		try {
			tempUploadedVideoPath = Files.createTempFile("upload", ".video");
			form.getFile().transferTo(tempUploadedVideoPath.toFile());
			tempUploadedVideoPathStorage.set(tempUploadedVideoPath);
			return pjp.proceed();
		} catch (IOException e) {
			throw new CantProcessMediaContentException("Can't save video content to temp file: " + e.getMessage(), e);
		} finally {
			tempUploadedVideoPathStorage.remove();
			if (tempUploadedVideoPath != null) {
				try {
					Files.deleteIfExists(tempUploadedVideoPath);
				} catch (Exception e) {
					LOGGER.warn("Can't remove temp file: " + tempUploadedVideoPath, e);
				}
			}
		}
	}
}
