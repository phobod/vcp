package com.phobod.study.vcp.service;

import javax.annotation.Nonnull;

import org.springframework.web.multipart.MultipartFile;

import com.phobod.study.vcp.exception.CantProcessMediaContentException;

public interface ImageService {
	@Nonnull String saveImageData (@Nonnull byte[] imageBytes) throws CantProcessMediaContentException;
	@Nonnull String saveImageData (@Nonnull MultipartFile multipartFile) throws CantProcessMediaContentException;
}
