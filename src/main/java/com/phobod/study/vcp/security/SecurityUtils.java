package com.phobod.study.vcp.security;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.phobod.study.vcp.domain.User;

public class SecurityUtils {

	public static @Nullable User getCurrentUser() {
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

	public static void addPrincipalHeaders(HttpServletResponse resp) {
		User user = SecurityUtils.getCurrentUser();
		if (user != null) {
			resp.setHeader("PrimcipalName", user.getName());
			resp.setHeader("PrimcipalRole", user.getRole().name());
		}
	}
	
	public static void authenticateAccount (@Nonnull User user) {
        CurrentUser currentUser = new CurrentUser(user);
        Authentication authentication =  new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
