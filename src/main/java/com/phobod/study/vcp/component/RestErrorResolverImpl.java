package com.phobod.study.vcp.component;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phobod.study.vcp.exception.CantProcessAccessRecoveryException;
import com.phobod.study.vcp.exception.CantProcessMediaContentException;
import com.phobod.study.vcp.exception.CantProcessStatisticsException;
import com.phobod.study.vcp.exception.CantProcessUserException;
import com.phobod.study.vcp.exception.ValidationException;

@Component
public class RestErrorResolverImpl implements RestErrorResolver {

	@Override
	public RestError resolveError(HttpServletRequest request, Object handler, Exception ex) throws IOException {
		if (ex instanceof NoSuchRequestHandlingMethodException || ex instanceof NoHandlerFoundException) {
			return handleNoHandlerFoundException(ex, request, handler);
		} else if (ex instanceof HttpRequestMethodNotSupportedException) {
			return handleHttpRequestMethodNotSupported(ex, request, handler);
		} else if (ex instanceof HttpMediaTypeNotSupportedException) {
			return handleHttpMediaTypeNotSupported(ex, request, handler);
		} else if (ex instanceof HttpMediaTypeNotAcceptableException) {
			return handleHttpMediaTypeNotAcceptable(ex, request, handler);
		} else if (ex instanceof MissingServletRequestParameterException || ex instanceof ServletRequestBindingException || ex instanceof TypeMismatchException
				|| ex instanceof HttpMessageNotReadableException || ex instanceof MethodArgumentNotValidException || ex instanceof MissingServletRequestPartException
				|| ex instanceof BindException) {
			return handleBadRequest(ex, request, handler);
		} else if (ex instanceof ValidationException) {
			return handleValidationError(ex, request, handler);
		} else if (ex instanceof CantProcessUserException) {
			return handleProcessUserError(ex, request, handler);
		} else if (ex instanceof CantProcessAccessRecoveryException) {
			return handleProcessPasswordRestoreError(ex, request, handler);
		} else if (ex instanceof CantProcessMediaContentException) {
			return handleProcessMediaContentError(ex, request, handler);
		} else if (ex instanceof CantProcessStatisticsException) {
			return handleProcessStatisticsError(ex, request, handler);
		} else {
			return handleInternalServerError(ex, request, handler);
		}
	}

	private RestError handleNoHandlerFoundException(Exception ex, HttpServletRequest request, Object handler) {
		return new RestError(HttpServletResponse.SC_NOT_FOUND, "Page Not Found", "The requested resource could not be found but may be available in the future");
	}

	private RestError handleHttpRequestMethodNotSupported(Exception ex, HttpServletRequest request, Object handler) {
		return new RestError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method Not Allowed", "A request method is not supported for the requested resource");
	}

	private RestError handleHttpMediaTypeNotSupported(Exception ex, HttpServletRequest request, Object handler) {
		return new RestError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type", "The request entity has a media type which the server or resource does not support.");
	}

	private RestError handleHttpMediaTypeNotAcceptable(Exception ex, HttpServletRequest request, Object handler) {
		return new RestError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Not Acceptable", "The requested resource is capable of generating only content not acceptable according to the Accept headers sent in the request.");
	}

	private RestError handleBadRequest(Exception ex, HttpServletRequest request, Object handler) {
		return new RestError(HttpServletResponse.SC_BAD_REQUEST, "Internal Server Error", "The server cannot or will not process the request due to an apparent client error (e.g., malformed request syntax, invalid request message framing, or deceptive request routing).");
	}

	private RestError handleValidationError(Exception ex, HttpServletRequest request, Object handler) throws IOException {
		if (ex.getCause() instanceof DuplicateKeyException) {
			return new RestError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not valid", getDescriptionFromDuplicateKeyException(ex));
		}
		return new RestError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during processing", "An error occurred during the processing or incorrect data. Please try again.");
	}

	private RestError handleProcessUserError(Exception ex, HttpServletRequest request, Object handler) {
		return new RestError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during processing", "An error occurred during the process of creating a new account. Please try again.");
	}

	private RestError handleProcessPasswordRestoreError(Exception ex, HttpServletRequest request, Object handler) {
		return new RestError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not correct data", "An error occurred during the process of access recovery. Please try again.");
	}

	private RestError handleProcessMediaContentError(Exception ex, HttpServletRequest request, Object handler) {
		return new RestError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not correct data", "An error occurred during the processing of the media content. Please try again.");
	}

	private RestError handleProcessStatisticsError(Exception ex, HttpServletRequest request, Object handler) {
		return new RestError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during processing", "An error occurred during the processing of the statistics. Please try again.");
	}

	private RestError handleInternalServerError(Exception ex, HttpServletRequest request, Object handler) {
		return new RestError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error", "We're sorry! The server encountered an internal error and was unable to complete your request.");
	}

	private String getDescriptionFromDuplicateKeyException(Exception ex) throws IOException, JsonParseException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		String errorText = mapper.readValue(ex.getCause().getMessage(), JsonNode.class).get("err").textValue();
		String paramName = findParamName(errorText);
		String paramValue = findParamValue(errorText);
		if (paramName != null && paramValue != null) {
			return "Can't save the record. The database already has an entry with parameter '" + paramName + "' = '" + paramValue + "'!";
		} else {
			return "Can't save the record. The database already has an entry with same parameter!";
		}
	}

	private String findParamName(String errorText) {
		if (errorText.split("index: ").length > 1) {
			if (errorText.split("index: ")[1].split(" ").length > 0) {
				return errorText.split("index: ")[1].split(" ")[0];
			}
		}
		return null;
	}

	private String findParamValue(String errorText) {
		if (errorText.split("\"").length > 0) {
			return errorText.split("\"")[1];
		}
		return null;
	}

}
