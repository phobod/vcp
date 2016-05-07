package com.phobod.study.vcp.service;

import javax.annotation.Nonnull;

import com.phobod.study.vcp.domain.User;

public interface NotificationService {
	void sendRestoreAccessLink(@Nonnull User profile, @Nonnull String restoreLink);
}
