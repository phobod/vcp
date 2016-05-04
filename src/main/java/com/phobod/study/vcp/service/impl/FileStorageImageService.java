package com.phobod.study.vcp.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.service.ImageService;

@Service
public class FileStorageImageService implements ImageService {

	@Value("${media.dir}")
	private String mediaDir;
	
	@Override
	public String saveImageData(byte[] imageBytes) throws CantProcessMediaContentException {
		try {
			return saveImageDataInternal(imageBytes);
		} catch (IOException e) {
			throw new CantProcessMediaContentException("Can't save image data: " + e.getMessage(), e);
		}
	}

	private String saveImageDataInternal(byte[] imageBytes) throws IOException {
		String uniquieThumbnailFileName = generateUniquieThumbnailFileName();
		Path path = Paths.get(mediaDir + "/thumbnail/"+uniquieThumbnailFileName);
		Files.write(path, imageBytes);
		return "/media/thumbnail/" + uniquieThumbnailFileName;
	}

	private String generateUniquieThumbnailFileName() {
		return UUID.randomUUID() + ".jpg";
	}

	@Override
	public String saveImageData(MultipartFile multipartFile) throws CantProcessMediaContentException {
		try {
			return saveImageDataInternal(multipartFile);
		} catch (IOException e) {
			throw new CantProcessMediaContentException("Save image failed: " + e.getMessage(), e);
		}
	}

	private String saveImageDataInternal(MultipartFile multipartFile) throws IOException{
		String uniqueImageFileName = generateUniquieThumbnailFileName();
		Path path = Paths.get(mediaDir + "/image/" + uniqueImageFileName);
		multipartFile.transferTo(path.toFile());
		return "/media/image/" + uniqueImageFileName;
	}
	
}
