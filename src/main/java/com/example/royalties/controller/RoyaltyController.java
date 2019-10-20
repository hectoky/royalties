package com.example.royalties.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.royalties.model.PaymentResponse;
import com.example.royalties.model.ViewRequest;
import com.example.royalties.service.PaymentService;
import com.example.royalties.service.ViewService;


@RestController
@RequestMapping(value = "/royaltymanager")
public class RoyaltyController {

	private static Logger logger = LoggerFactory.getLogger(RoyaltyController.class);

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private ViewService viewService;



	@RequestMapping(value = "/viewing", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Void> viewing(HttpServletRequest request, @RequestBody ViewRequest viewRequest)
			throws ValidationException {
		
		viewService.validation(viewRequest);
		logger.info("Request received in /viewing endpoint with data {}", viewRequest);
		viewService.view(viewRequest.getEpisode());
		
		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }

	@RequestMapping(value = "/reset", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Void> reset(HttpServletRequest request) {
		
		logger.info("Request received in /reset endpoint");
		viewService.reset();
		
		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
	}
    
	@RequestMapping(value = "/payments", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<List<PaymentResponse>> getAllPayments(HttpServletRequest request) {

		logger.info("Request received in /payments endpoint");
		return ResponseEntity.ok(paymentService.getAllPayment());
	}

	@RequestMapping(value = "/payments/{studioReference}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<PaymentResponse> getPaymentByRefence(HttpServletRequest request,
			@PathVariable String studioReference)
			throws ValidationException {
		logger.info("Request received in /payments/{} endpoint", studioReference);
		PaymentResponse result = paymentService.getPaymentById(studioReference);


		return ResponseEntity.ok(result);

	}


}
