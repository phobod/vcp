package com.phobod.study.vcp.service;

import javax.annotation.Nonnull;

public interface AvatarService {
	@Nonnull String generateAvatarUrl(@Nonnull String email);
}
