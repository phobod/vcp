package com.phobod.study.vcp.component;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.phobod.study.vcp.controller.CommonController;
import com.phobod.study.vcp.exception.CantProcessAccessRecoveryException;
import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.exception.CantProcessStatisticsException;
import com.phobod.study.vcp.exception.CantProcessUserException;
import com.phobod.study.vcp.exception.ValidationException;

@RunWith(MockitoJUnitRunner.class)
public class RestErrorResolverImplTest {
	private RestErrorResolver restErrorResolver = new RestErrorResolverImpl();

	@Test
	public final void testResolveErrorNotFound() throws IOException {
		RestError restError = restErrorResolver.resolveError(null, null, new NoSuchRequestHandlingMethodException("", CommonController.class));
		assertEquals(HttpServletResponse.SC_NOT_FOUND, restError.getStatus());
		restError = restErrorResolver.resolveError(null, null, new NoHandlerFoundException("", "", null));
		assertEquals(HttpServletResponse.SC_NOT_FOUND, restError.getStatus());
	}

	@Test
	public final void testResolveErrorMethodNotAllowed() throws IOException {
		RestError restError = restErrorResolver.resolveError(null, null, new HttpRequestMethodNotSupportedException(""));
		assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, restError.getStatus());
	}

	@Test
	public final void testResolveErrorUnsupportedMediaType() throws IOException {
		RestError restError = restErrorResolver.resolveError(null, null, new HttpMediaTypeNotSupportedException(""));
		assertEquals(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, restError.getStatus());
	}

	@Test
	public final void testResolveErrorNotAcceptable() throws IOException {
		RestError restError = restErrorResolver.resolveError(null, null, new HttpMediaTypeNotAcceptableException(""));
		assertEquals(HttpServletResponse.SC_NOT_ACCEPTABLE, restError.getStatus());
	}

	@Test
	public final void testResolveErrorBadRequest() throws IOException {
		RestError restError = restErrorResolver.resolveError(null, null, new MissingServletRequestParameterException("", ""));
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, restError.getStatus());
		restError = restErrorResolver.resolveError(null, null, new ServletRequestBindingException(""));
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, restError.getStatus());
		restError = restErrorResolver.resolveError(null, null, new TypeMismatchException("", Object.class));
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, restError.getStatus());
		restError = restErrorResolver.resolveError(null, null, new HttpMessageNotReadableException(""));
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, restError.getStatus());
		restError = restErrorResolver.resolveError(null, null, new MissingServletRequestPartException(""));
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, restError.getStatus());
	}

	@Test
	public final void testResolveErrorInternalServerError() throws IOException {
		Throwable cause = new DuplicateKeyException("{ \"serverUsed\" : \"localhost:27017\" , \"ok\" : 1 , \"n\" : 0 , \"err\" : \"E11000 duplicate key error collection: vcp.company index: name dup key: { : \\\"VCP\\\" }\" , \"code\" : 11000}");
		RestError restError = restErrorResolver.resolveError(null, null, new ValidationException("ValidationException", cause));
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, restError.getStatus());
		restError = restErrorResolver.resolveError(null, null, new CantProcessUserException(""));
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, restError.getStatus());
		restError = restErrorResolver.resolveError(null, null, new CantProcessAccessRecoveryException(""));
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, restError.getStatus());
		restError = restErrorResolver.resolveError(null, null, new CantProcessMediaContentException(""));
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, restError.getStatus());
		restError = restErrorResolver.resolveError(null, null, new CantProcessStatisticsException(""));
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, restError.getStatus());
		restError = restErrorResolver.resolveError(null, null, new RuntimeException(""));
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, restError.getStatus());
	}

}
