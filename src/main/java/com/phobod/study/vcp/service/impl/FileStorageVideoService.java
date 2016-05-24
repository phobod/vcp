package com.phobod.study.vcp.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.service.VideoService;

@Service
public class FileStorageVideoService implements VideoService {
	@Value("${media.dir}")
	private String mediaDir;

	@Override
	public String saveVideo(Path tempFilePath) {
		try {
			return saveVideoInternal(tempFilePath);
		} catch (IOException e) {
			throw new CantProcessMediaContentException("save video failed: " + e.getMessage(), e);
		}
	}

	private String saveVideoInternal(Path tempFilePath) throws IOException {
		if (tempFilePath == null) {
			throw new CantProcessMediaContentException("Can't save video. File path is Null");
		}
		String uniqueVideoFileName = generateUniqueVideoFileName();
		Path videoFilePath = Paths.get(mediaDir + "/video/" + uniqueVideoFileName);
		Files.copy(tempFilePath, videoFilePath);
		return "/media/video/" + uniqueVideoFileName;
	}

	private String generateUniqueVideoFileName() {
		return UUID.randomUUID() + ".mp4";
	}

}
