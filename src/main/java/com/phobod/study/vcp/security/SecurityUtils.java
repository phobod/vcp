package com.phobod.study.vcp.security;

import com.phobod.study.vcp.domain.User;
import com.phobod.study.vcp.service.impl.CreateTestDataService;

public class SecurityUtils {
	
	public static User getCurrentUser(){
		return CreateTestDataService.getTestUser();
	}
}
