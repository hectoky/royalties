package com.example.royalties.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;



@ControllerAdvice
public class RoyaltyControllerAdvice {

	private static Logger logger = LoggerFactory.getLogger(RoyaltyControllerAdvice.class);

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Object handleValidationException(ValidationException e, HttpServletRequest request,
			HttpServletResponse response) {
		logger.error(e.getMessage(), e);
		return e.getMessage();
	}



}
