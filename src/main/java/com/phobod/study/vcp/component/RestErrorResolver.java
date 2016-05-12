package com.phobod.study.vcp.component;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public interface RestErrorResolver {
	RestError resolveError(HttpServletRequest request, Object handler, Exception ex) throws IOException;
}
