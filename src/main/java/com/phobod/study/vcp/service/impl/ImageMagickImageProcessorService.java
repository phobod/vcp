package com.phobod.study.vcp.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.component.ExternalApplicationUtils;
import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.service.ImageProcessorService;

@Service
public class ImageMagickImageProcessorService implements ImageProcessorService{
	@Value("${imageMagicConvert}")
	private String imageMagicConvert;
	
	@Value("${jpegtran}")
	private String jpegtran;
	
	@Override
	public void resizeImageFile(String sourceFile, String targerFile, String size) throws CantProcessMediaContentException {
		try {
			resizeImageFileInternal(sourceFile, targerFile, size);
		} catch (IOException | InterruptedException e) {
			throw new CantProcessMediaContentException("Resize image file failed: " + e.getMessage(), e);
		}
	}

	private void resizeImageFileInternal(String sourceFile, String targerFile, String size) throws IOException, InterruptedException {
		if (sourceFile == null || targerFile == null || size == null) {
			throw new CantProcessMediaContentException("Resize image failed. Source Image file name, Target Image file name or size is Null");
		}
		ProcessBuilder pb = new ProcessBuilder(imageMagicConvert, "-size", size, "-thumbnail", size+"^", "-crop" , size+"+0+0", "+repage", "-gravity", "center", sourceFile, targerFile);
        String resultExternalExecutionCommand = ExternalApplicationUtils.execution(pb);
		if (resultExternalExecutionCommand == null){
        	throw new CantProcessMediaContentException("Resize image failed. Error during processing: " + resultExternalExecutionCommand);
        }
	}

	@Override
	public void optimizeJpegImageFile(String targerFile) throws CantProcessMediaContentException{
		try {
			optimizeJpegImageFileInternal(targerFile);
		} catch (IOException | InterruptedException e) {
			throw new CantProcessMediaContentException("Optimize jpeg image file failed: " + e.getMessage(), e);
		}		
	}

	private void optimizeJpegImageFileInternal(String targerFile) throws IOException, InterruptedException {
		if (targerFile == null) {
			throw new CantProcessMediaContentException("Optimize image failed. Image file name is Null");
		}
		ProcessBuilder pb = new ProcessBuilder(jpegtran, "-progressive", targerFile);
		ExternalApplicationUtils.execution(pb);
	}

}
