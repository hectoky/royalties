package com.example.royalties.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.validation.ValidationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.royalties.RoyaltiesApplication;
import com.example.royalties.model.PaymentResponse;
import com.example.royalties.model.ViewRequest;
import com.example.royalties.service.PaymentService;
import com.example.royalties.service.ViewService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { RoyaltiesApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class RoyaltyControllerTest {

	@MockBean
	private ViewService viewService;

	@MockBean
	private PaymentService paymentService;

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void shouldViewingEndpointOk_whenServiceOk() {
		ViewRequest input = new ViewRequest("episode", "customer");
		Mockito.doNothing().when(viewService).validation(any(ViewRequest.class));
		Mockito.doNothing().when(viewService).view(input.getEpisode());
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<ViewRequest> entity = new HttpEntity<>(input, headers);
		ResponseEntity<Void> response = testRestTemplate.postForEntity("/royaltymanager/viewing", entity,
				Void.class);

		Mockito.verify(viewService, times(1)).validation(any(ViewRequest.class));
		Mockito.verify(viewService, times(1)).view(input.getEpisode());
		Assert.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		Assert.assertTrue(response.getBody() == null);

	}

	@Test
	public void shouldViewingEndpointKO_whenValidationFail() {
		ViewRequest input = new ViewRequest("episode", null);
		Mockito.doThrow(ValidationException.class).when(viewService).validation(any(ViewRequest.class));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<ViewRequest> entity = new HttpEntity<>(input, headers);
		ResponseEntity<Void> response = testRestTemplate.postForEntity("/royaltymanager/viewing", entity, Void.class);

		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

	}

	@Test
	public void shouldViewingEndpointKO_whenSendWithNullBody() {
		Mockito.doThrow(ValidationException.class).when(viewService).validation(any(ViewRequest.class));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<ViewRequest> entity = new HttpEntity<>(null, headers);
		ResponseEntity<Void> response = testRestTemplate.postForEntity("/royaltymanager/viewing", entity, Void.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

	}

	@Test
	public void shouldResetEndpointOk() {
		Mockito.doNothing().when(viewService).reset();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<Void> response = testRestTemplate.postForEntity("/royaltymanager/reset", entity, Void.class);

		Mockito.verify(viewService, times(1)).reset();
		Assert.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		Assert.assertTrue(response.getBody() == null);

	}

	@Test
	public void shouldGetAllPaymentsOk() {
		PaymentResponse payment1 = new PaymentResponse("Ref1", "Name1", BigDecimal.valueOf(100), 1L);
		PaymentResponse payment2 = new PaymentResponse("Ref2", "Name2", BigDecimal.valueOf(200), 2L);
		List<PaymentResponse> expectedResult = Arrays.asList(payment1, payment2);
		Mockito.when(paymentService.getAllPayment()).thenReturn(expectedResult);
		Mockito.doNothing().when(viewService).reset();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ParameterizedTypeReference<List<PaymentResponse>> typeRef = new ParameterizedTypeReference<List<PaymentResponse>>() {
		};

		ResponseEntity<List<PaymentResponse>> response = testRestTemplate.exchange("/royaltymanager/payments", HttpMethod.GET, entity, typeRef);

		Mockito.verify(paymentService, times(1)).getAllPayment();
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertEquals(2, response.getBody().size());
		Assert.assertEquals(payment1.getRightsowner(), response.getBody().get(0).getRightsowner());
		Assert.assertEquals(payment1.getRightsownerId(), response.getBody().get(0).getRightsownerId());
		Assert.assertEquals(payment1.getRoyalty(), response.getBody().get(0).getRoyalty());
		Assert.assertEquals(payment1.getViewings(), response.getBody().get(0).getViewings());
		Assert.assertEquals(payment2.getRightsowner(), response.getBody().get(1).getRightsowner());
		Assert.assertEquals(payment2.getRightsownerId(), response.getBody().get(1).getRightsownerId());
		Assert.assertEquals(payment2.getRoyalty(), response.getBody().get(1).getRoyalty());
		Assert.assertEquals(payment2.getViewings(), response.getBody().get(1).getViewings());

	}

	@Test
	public void shouldGetPaymentsByIdOk() throws JsonMappingException, JsonProcessingException {
		String reference = "reference";
		PaymentResponse expectedResult = new PaymentResponse(null, "Name1", BigDecimal.valueOf(100), 1L);
		Mockito.when(paymentService.getPaymentById(reference)).thenReturn(expectedResult);
		Mockito.doNothing().when(viewService).reset();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = testRestTemplate.exchange("/royaltymanager/payments/" + reference,
				HttpMethod.GET, entity, String.class);

		Mockito.verify(paymentService, times(1)).getPaymentById(reference);
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertTrue(!response.getBody().contains("rightsownerId"));
		PaymentResponse actualResponse = objectMapper.readValue(response.getBody(), PaymentResponse.class);
		Assert.assertEquals(expectedResult.getRightsowner(), actualResponse.getRightsowner());
		Assert.assertEquals(expectedResult.getRoyalty(), actualResponse.getRoyalty());
		Assert.assertEquals(expectedResult.getViewings(), actualResponse.getViewings());

	}

	@Test
	public void shouldGetPaymentsByIdKO_whenValidationFails() {
		String reference = "reference";
		Mockito.when(paymentService.getPaymentById(reference)).thenThrow(ValidationException.class);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = testRestTemplate.exchange("/royaltymanager/payments/" + reference,
				HttpMethod.GET, entity, String.class);

		Mockito.verify(paymentService, times(1)).getPaymentById(reference);
		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

	}

}
