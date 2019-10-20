package com.example.royalties;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.royalties.model.PaymentResponse;
import com.example.royalties.model.ViewRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { RoyaltiesApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class SmokeTest {
	/*
	 * This kind of classes normally should not be in the test
	 * 
	 */

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	public void someTests() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ParameterizedTypeReference<List<PaymentResponse>> typeRef = new ParameterizedTypeReference<List<PaymentResponse>>() {
		};

		// 1 - Check the reset endpoint
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<Void> resetResponse = testRestTemplate.postForEntity("/royaltymanager/reset", entity,
				Void.class);
		Assert.assertEquals(HttpStatus.ACCEPTED, resetResponse.getStatusCode());
		Assert.assertTrue(resetResponse.getBody() == null);

		// 2 - All the owners should have the view counter to 0
		ResponseEntity<List<PaymentResponse>> allPaymentsResponse = testRestTemplate
				.exchange("/royaltymanager/payments", HttpMethod.GET, entity, typeRef);

		Assert.assertEquals(HttpStatus.OK, allPaymentsResponse.getStatusCode());
		Assert.assertEquals(4, allPaymentsResponse.getBody().size());
		allPaymentsResponse.getBody().forEach(payment -> Assert.assertTrue(payment.getViewings().equals(0L)));

		// 3 - Do a view for two episodes of different owners
		ViewRequest input = new ViewRequest("6a1db5d6610a4c048d3df9a6268c68dc", "customer"); // Owner: HBO
																								// 665115721c6f44e49be3bd3e26606026
		HttpEntity<ViewRequest> entityView = new HttpEntity<>(input, headers);
		ResponseEntity<Void> viewResponse = testRestTemplate.postForEntity("/royaltymanager/viewing", entityView,
				Void.class);
		Assert.assertEquals(HttpStatus.ACCEPTED, viewResponse.getStatusCode());
		Assert.assertTrue(viewResponse.getBody() == null);

		input = new ViewRequest("fcfba01219464541a70eb8677260de4d", "customer"); // Owner: Sky UK
																					// 8d713a092ebf4844840cb90d0c4a2030
		entityView = new HttpEntity<>(input, headers);
		viewResponse = testRestTemplate.postForEntity("/royaltymanager/viewing", entityView, Void.class);
		Assert.assertEquals(HttpStatus.ACCEPTED, viewResponse.getStatusCode());
		Assert.assertTrue(viewResponse.getBody() == null);

		// 4 - Check the payments for this two owners
		String studioRef = "665115721c6f44e49be3bd3e26606026";
		ResponseEntity<PaymentResponse> paymentByIdResponse = testRestTemplate
				.exchange("/royaltymanager/payments/" + studioRef, HttpMethod.GET, entity, PaymentResponse.class);
		Assert.assertEquals(HttpStatus.OK, paymentByIdResponse.getStatusCode());
		Assert.assertEquals(1, paymentByIdResponse.getBody().getViewings().longValue());
		// HBO has a payment 12
		Assert.assertTrue(paymentByIdResponse.getBody().getRoyalty().compareTo(BigDecimal.valueOf(12.00)) == 0);

		studioRef = "8d713a092ebf4844840cb90d0c4a2030";
		paymentByIdResponse = testRestTemplate.exchange("/royaltymanager/payments/" + studioRef, HttpMethod.GET, entity,
				PaymentResponse.class);
		Assert.assertEquals(HttpStatus.OK, paymentByIdResponse.getStatusCode());
		Assert.assertEquals(1, paymentByIdResponse.getBody().getViewings().longValue());
		// Sky UK has a payment 14.67
		Assert.assertTrue(paymentByIdResponse.getBody().getRoyalty().compareTo(BigDecimal.valueOf(14.67)) == 0);

		// 5 - Do a view again in HBO and check it
		input = new ViewRequest("a4cfda21457e4e548f2c3a472decc7cb", "customer"); // Owner: HBO
																					// 665115721c6f44e49be3bd3e26606026
		entityView = new HttpEntity<>(input, headers);
		viewResponse = testRestTemplate.postForEntity("/royaltymanager/viewing", entityView,
				Void.class);
		Assert.assertEquals(HttpStatus.ACCEPTED, viewResponse.getStatusCode());
		Assert.assertTrue(viewResponse.getBody() == null);

		studioRef = "665115721c6f44e49be3bd3e26606026";
		paymentByIdResponse = testRestTemplate.exchange("/royaltymanager/payments/" + studioRef, HttpMethod.GET, entity,
				PaymentResponse.class);
		Assert.assertEquals(HttpStatus.OK, paymentByIdResponse.getStatusCode());
		Assert.assertEquals(2, paymentByIdResponse.getBody().getViewings().longValue());
		// HBO has a payment 12
		Assert.assertTrue(paymentByIdResponse.getBody().getRoyalty().compareTo(BigDecimal.valueOf(24.00)) == 0);

		// 6 - Check that when we call to allPayment now HBO and Sky UK have changed
		// since last call to this endpoint
		allPaymentsResponse = testRestTemplate.exchange("/royaltymanager/payments", HttpMethod.GET, entity, typeRef);

		Assert.assertEquals(HttpStatus.OK, allPaymentsResponse.getStatusCode());
		Assert.assertEquals(4, allPaymentsResponse.getBody().size());
		allPaymentsResponse.getBody().forEach(payment -> {
			if (payment.getRightsownerId().equals("665115721c6f44e49be3bd3e26606026"))
				Assert.assertTrue(payment.getViewings().equals(2L));
			else if (payment.getRightsownerId().equals("8d713a092ebf4844840cb90d0c4a2030"))
				Assert.assertTrue(payment.getViewings().equals(1L));
			else
				Assert.assertTrue(payment.getViewings().equals(0L));
		});

		// 7 - Validation fails in view when not found episode reference
		input = new ViewRequest("a4cfda21457e4e548f2c3a4AAAAAAc7cb", "customer");
		entityView = new HttpEntity<>(input, headers);
		viewResponse = testRestTemplate.postForEntity("/royaltymanager/viewing", entityView, Void.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, viewResponse.getStatusCode());

		// 8 - Validation fails in view when not set customer id
		input = new ViewRequest("a4cfda21457e4e548f2c3a472decc7cb", null);
		entityView = new HttpEntity<>(input, headers);
		viewResponse = testRestTemplate.postForEntity("/royaltymanager/viewing", entityView, Void.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, viewResponse.getStatusCode());

		// 9 - Validation fails in view when not set episode reference
		input = new ViewRequest(null, "customer");
		entityView = new HttpEntity<>(input, headers);
		viewResponse = testRestTemplate.postForEntity("/royaltymanager/viewing", entityView, Void.class);

		// 10 - Validation fails in getPaymentById when not found episode reference
		entity = new HttpEntity<>(null, headers);
		studioRef = "665115721c6f44e49be3bd3e266060";
		ResponseEntity<String> responseValidation = testRestTemplate.exchange("/royaltymanager/payments/" + studioRef,
				HttpMethod.GET, entity,
				String.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, viewResponse.getStatusCode());

		// 11 - Finish call to reset and check that all the views are set to 0
		entity = new HttpEntity<>(null, headers);
		resetResponse = testRestTemplate.postForEntity("/royaltymanager/reset", entity, Void.class);
		Assert.assertEquals(HttpStatus.ACCEPTED, resetResponse.getStatusCode());
		Assert.assertTrue(resetResponse.getBody() == null);

		allPaymentsResponse = testRestTemplate.exchange("/royaltymanager/payments", HttpMethod.GET, entity, typeRef);

		Assert.assertEquals(HttpStatus.OK, allPaymentsResponse.getStatusCode());
		Assert.assertEquals(4, allPaymentsResponse.getBody().size());
		allPaymentsResponse.getBody().forEach(payment -> Assert.assertTrue(payment.getViewings().equals(0L)));
	}
}
