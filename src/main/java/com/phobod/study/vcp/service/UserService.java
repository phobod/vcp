package com.phobod.study.vcp.service;

import javax.annotation.Nonnull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.domain.Video;
import com.phobod.study.vcp.form.VideoUploadForm;

public interface UserService {
	@Nonnull Page<Video> listVideosByUser(@Nonnull Pageable pageable, @Nonnull String userId);
	@Nonnull Page<Video> listVideosByUserExcludeOne(@Nonnull Pageable pageable, @Nonnull String excludedVideoId, @Nonnull String userId);
	void uploadVideo(@Nonnull User currentUser, @Nonnull VideoUploadForm form);
	void updateVideo(@Nonnull String videoId, @Nonnull VideoUploadForm form, @Nonnull String userId) throws AccessDeniedException;
	void deleteVideo(@Nonnull String videoId, @Nonnull String userId) throws AccessDeniedException;
	void deleteAllVideosByUser(@Nonnull String userId);
}
