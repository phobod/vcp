package com.phobod.study.vcp.service.impl;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.component.UploadVideoTempStorage;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.VideoUploadForm;
import com.phobod.study.vcp.service.ImageProcessorService;
import com.phobod.study.vcp.service.ImageService;
import com.phobod.study.vcp.service.ThumbnailService;
import com.phobod.study.vcp.service.VideoProcessorService;
import com.phobod.study.vcp.service.VideoService;

@Service("simpleVideoProcessorService")
public class SimpleVideoProcessorService implements VideoProcessorService {

	@Value("${media.dir}")
	private String mediaDir;

	@Value("${image.medium.size}")
	private String imageMediumSize;

	@Value("${image.small.size}")
	private String imageSmallSize;

	@Autowired
	private VideoService videoService;

	@Autowired
	@Qualifier("ffmpegThumbnailService")
	private ThumbnailService thumbnailService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private ImageProcessorService imageProcessorService;

	@Autowired
	private UploadVideoTempStorage uploadVideoTempStorage;

	@Override
	public Video processVideo(VideoUploadForm uploadForm) {
		Path tempUploadedVideoPath = uploadVideoTempStorage.getTempUploadedVideoPath(uploadForm);
		String videoUrl = videoService.saveVideo(tempUploadedVideoPath);
		byte[] thumbnailImageData = thumbnailService.createThumbnail(tempUploadedVideoPath);
		String thumbnailImageUrl = imageService.saveImageData(thumbnailImageData);
		resizeAndOptimizeImage(thumbnailImageUrl);
		return new Video(thumbnailImageUrl, thumbnailImageUrl.replace("/media/thumbnail/", "/media/thumbnail/medium-"), thumbnailImageUrl.replace("/media/thumbnail/", "/media/thumbnail/small-"), videoUrl);
	}

	private void resizeAndOptimizeImage(String thumbnailImageUrl) {
		String sourceFileAbsolutePath = mediaDir.substring(0, mediaDir.lastIndexOf("/media")) + thumbnailImageUrl;
		String targerMediumFileAbsolutePath = mediaDir + "/thumbnail/medium-" + thumbnailImageUrl.replace("/media/thumbnail/", "");
		String targerSmallFileAbsolutePath = mediaDir + "/thumbnail/small-" + thumbnailImageUrl.replace("/media/thumbnail/", "");
		imageProcessorService.optimizeJpegImageFile(sourceFileAbsolutePath);
		imageProcessorService.resizeImageFile(sourceFileAbsolutePath, targerMediumFileAbsolutePath, imageMediumSize);
		imageProcessorService.optimizeJpegImageFile(targerMediumFileAbsolutePath);
		imageProcessorService.resizeImageFile(sourceFileAbsolutePath, targerSmallFileAbsolutePath, imageSmallSize);
		imageProcessorService.optimizeJpegImageFile(targerSmallFileAbsolutePath);
	}

}
