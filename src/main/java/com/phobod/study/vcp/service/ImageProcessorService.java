package com.phobod.study.vcp.service;

import javax.annotation.Nonnull;

import com.phobod.study.vcp.exception.CantProcessMediaContentException;

public interface ImageProcessorService {
	void resizeImageFile (@Nonnull String sourrceFile, @Nonnull String targerFile, @Nonnull String size) throws CantProcessMediaContentException;
	void optimizeJpegImageFile (@Nonnull String targerFile) throws CantProcessMediaContentException;
}
