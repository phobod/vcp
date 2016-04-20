package com.phobod.study.vcp.service.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.exception.ApplicationException;
import com.phobod.study.vcp.service.ThumbnailService;
import com.phobod.study.vcp.service.VideoService;

@Service
public class FileStorageVideoService implements VideoService{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageVideoService.class);
	
	@Autowired
	private ThumbnailService thumbnailService;
	
	@Value("${media.dir}")
	private String mediaDir;

	@Override
	public Video processVideo(MultipartFile videoFile) {
		try {
			return processVidoeInternal(videoFile);
		} catch (IOException e) {
			throw new ApplicationException("save video failed: " + e.getMessage(), e);
		}
	}

	private Video processVidoeInternal(MultipartFile multipartVideoFile) throws IOException {
		String uniqueVideoFileName = generateUniqueVideoFileName();
		Path videoFilePath = saveMultipartFile(multipartVideoFile, uniqueVideoFileName);
		String thumbnail = thumbnailService.createThumbnail(videoFilePath);
		LOGGER.info("New video {} uploaded", videoFilePath.getFileName());
		return new Video(thumbnail, "/media/video/" + uniqueVideoFileName);
	}

	private Path saveMultipartFile(MultipartFile multipartVideoFile, String uniqueVideoFileName) throws IOException {
		Path videoFilePath = Paths.get(mediaDir + "/video/" + uniqueVideoFileName);
		multipartVideoFile.transferTo(videoFilePath.toFile());
		return videoFilePath;
	}

	private String generateUniqueVideoFileName() {
		return UUID.randomUUID().toString() + ".mp4";
	}

}
