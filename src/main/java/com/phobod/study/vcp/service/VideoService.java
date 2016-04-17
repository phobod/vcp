package com.phobod.study.vcp.service;

import org.springframework.web.multipart.MultipartFile;

import com.phobod.study.vcp.domain.Video;

public interface VideoService {
	Video processVideo(MultipartFile videoFile);
}
