package com.fruits.service.handler;

import static com.fruits.common.ResponseCodeEnum.INVALID_PARAM;

import com.fruits.common.Response;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	protected static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseBody
	public Response<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		return Response.error("缺少必要参数: " + e.getParameterName());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public Response<?> handleArgumentNotValidException(MethodArgumentNotValidException e) {
		String msg = e.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(
				Collectors.joining(","));
		return Response.error("参数校验异常：" + msg);
	}

	@ExceptionHandler(BindException.class)
	public Response<?> bindException(BindException e) {
		String msg = e.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(
				Collectors.joining(","));
		return Response.error("参数校验异常：" + msg);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseBody
	public Response<?> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		return Response.errorBuilder().code(INVALID_PARAM.getCode()).debug(e.getMessage()).build();
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	@ResponseBody
	public Response<?> handleMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
		return Response.errorBuilder().code(INVALID_PARAM.getCode()).debug(e.getMessage()).build();
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseBody
	public Response<?> handleMessageNotReadableException(HttpMessageNotReadableException e) {
		return Response.errorBuilder().code(INVALID_PARAM.getCode()).debug(e.getMessage()).build();
	}


	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public Response<?> handleException(RuntimeException e) {
		log.error("RuntimeException: {}", e);
		return Response.error(e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public Response<?> handleException(Exception e) {
		log.error("Exception: {}", e);
		return Response.error(e.getMessage());
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	public Response<?> handleMissingRequestHeaderException(MissingRequestHeaderException e){
		return Response.errorBuilder().code(INVALID_PARAM.getCode()).debug("缺少必要请求头: " + e.getHeaderName()).build();
	}

}
