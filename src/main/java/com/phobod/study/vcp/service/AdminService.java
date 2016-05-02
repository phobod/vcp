package com.phobod.study.vcp.service;

import javax.annotation.Nonnull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.phobod.study.vcp.domain.User;

public interface AdminService {
	@Nonnull Page<User> listAllUsers(@Nonnull Pageable pageable);
}
