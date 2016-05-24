package com.phobod.study.vcp.service;

import java.nio.file.Path;

import javax.annotation.Nonnull;

import com.phobod.study.vcp.exception.CantProcessMediaContentException;

public interface VideoService {
	@Nonnull
	String saveVideo(@Nonnull Path tempFilePath) throws CantProcessMediaContentException;
}
