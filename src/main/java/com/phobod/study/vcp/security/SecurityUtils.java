package com.phobod.study.vcp.security;

import javax.annotation.Nullable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.phobod.study.vcp.domain.User;

public class SecurityUtils {
	
	public static @Nullable User getCurrentUser(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}
		Object principal = authentication.getPrincipal();
		if (principal instanceof CurrentUser) {
			return ((CurrentUser) principal).getUser();
		} else {
			return null;
		}
	}
}
