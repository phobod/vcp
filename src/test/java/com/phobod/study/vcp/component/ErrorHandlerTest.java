package com.phobod.study.vcp.component;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlerTest {
	@InjectMocks
	private ErrorHandler errorHandler = new ErrorHandler();

	@Mock
	private RestErrorResolver restErrorResolver;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	private RestError restError;
	private Exception ex;

	@Before
	public void setUp() throws Exception {
		restError = new RestError(500, "message", "description");
		ex = new RuntimeException("Exception");
	}

	@Test
	public final void testDoResolve() throws IOException {
		when(restErrorResolver.resolveError(request, null, ex)).thenReturn(restError);
		when(response.getWriter()).thenReturn(new PrintWriter(System.out));
		errorHandler.doResolveException(request, response, null, ex);
		verify(restErrorResolver).resolveError(request, null, ex);
		verify(response).reset();
		verify(response).setStatus(500);
	}

}
