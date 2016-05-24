package com.phobod.study.vcp.service;

import java.util.List;

import javax.annotation.Nonnull;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.domain.VideoStatistics;
import com.phobod.study.vcp.exception.CantProcessStatisticsException;

public interface VideoStatisticsService {
	void saveVideoViewStatistics(@Nonnull Video video, @Nonnull User user, @Nonnull String userIP);

	@Nonnull
	List<VideoStatistics> listVideoStatistics() throws CantProcessStatisticsException;

}
