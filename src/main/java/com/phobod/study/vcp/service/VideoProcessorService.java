package com.phobod.study.vcp.service;

import javax.annotation.Nonnull;

import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.VideoUploadForm;

public interface VideoProcessorService {
	@Nonnull Video processVideo(@Nonnull VideoUploadForm uploadForm);
}
