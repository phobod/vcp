package com.phobod.study.vcp.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.exception.CantProcessAccessRecoveryException;

public interface CommonService {
	@Nonnull
	List<Video> listPopularVideos();

	@Nonnull
	Page<Video> listAllVideos(@Nonnull Pageable pageable);

	@Nonnull
	Video findVideoById(@Nonnull String videoId, @Nonnull User user, @Nonnull String userIP);

	@Nonnull
	Page<Video> listVideosBySearchQuery(@Nonnull String query, @Nonnull Pageable pageable);

	void sendRestoreEmail(@Nonnull String login) throws CantProcessAccessRecoveryException;
	
	void checkRestorePasswordLink(@Nonnull String userId, @Nonnull String hash) throws CantProcessAccessRecoveryException;

	void restorePassword(@Nonnull User user, @Nonnull String password) throws CantProcessAccessRecoveryException;
}
