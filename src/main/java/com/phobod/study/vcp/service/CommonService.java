package com.phobod.study.vcp.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.phobod.study.vcp.domain.Video;

public interface CommonService {
	@Nonnull List<Video> listPopularVideos();
	@Nonnull Page<Video> listAllVideos(@Nonnull Pageable pageable);
	@Nonnull Video findVideoById(@Nonnull String id);
}
