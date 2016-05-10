package com.phobod.study.vcp.service;

import javax.annotation.Nonnull;

import com.phobod.study.vcp.domain.User;

public interface VideoStatisticsService {
	void saveVideoViewStatistics(@Nonnull String videoId, @Nonnull User user, @Nonnull String userIP);

}
