package com.phobod.study.vcp.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

@Component
public class ErrorHandler extends DefaultHandlerExceptionResolver {
	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);
	private static final String message = "{\"message\":\"%s\", \"description\":\"%s\"}";

	@Autowired
	RestErrorResolver restErrorResolver;

	public ErrorHandler() {
		setOrder(0);
	}

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		LOGGER.error("Error: " + ex.getMessage(), ex);
		try {
			RestError restError = restErrorResolver.resolveError(request, handler, ex);
			response.reset();
			response.setContentType("application/json");
			response.getWriter().write(String.format(message, restError.getMessage(), restError.getDescription()));
			response.setStatus(restError.getStatus());
		} catch (Exception e) {
			LOGGER.error("Error: " + e.getMessage(), e);
		}
		return new ModelAndView();
	}

}
