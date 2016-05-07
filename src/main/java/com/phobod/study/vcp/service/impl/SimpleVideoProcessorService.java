package com.phobod.study.vcp.service.impl;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.component.UploadVideoTempStorage;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.VideoUploadForm;
import com.phobod.study.vcp.service.ImageService;
import com.phobod.study.vcp.service.ThumbnailService;
import com.phobod.study.vcp.service.VideoProcessorService;
import com.phobod.study.vcp.service.VideoService;

@Service("simpleVideoProcessorService")
public class SimpleVideoProcessorService implements VideoProcessorService{
	
	@Autowired
	private VideoService videoService;
	
	@Autowired
	private ThumbnailService thumbnailService;
	
	@Autowired
	private ImageService imageService;
	
	@Autowired
	private UploadVideoTempStorage uploadVideoTempStorage;

	@Override
	public Video processVideo(VideoUploadForm uploadForm) {
		Path tempUploadedVideoPath = uploadVideoTempStorage.getTempUploadedVideoPath();
		String videoUrl = videoService.saveVideo(tempUploadedVideoPath);
		byte[] thumbnailImageData = thumbnailService.createThumbnail(tempUploadedVideoPath);
		String thumbnailImageUrl = imageService.saveImageData(thumbnailImageData);
		return new Video(thumbnailImageUrl, videoUrl);
	}

}
