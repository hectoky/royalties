package com.example.royalties.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.ValidationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.example.royalties.entity.StudioEntity;
import com.example.royalties.model.PaymentResponse;
import com.example.royalties.repository.StudioRepository;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceImplTest {

	private PaymentService service;

	@Mock
	private StudioRepository studioRepository;

	@Before
	public void setUp() {
		service = new PaymentServiceImpl(studioRepository);
	}

	@Test
	public void shouldPaymentByIdOk_whenFoundReference() {
		String input = "reference";
		StudioEntity studioEntity = new StudioEntity(1L, input, "Number1", BigDecimal.valueOf(3), 3L);
		Mockito.when(studioRepository.findByReference(input)).thenReturn(Optional.of(studioEntity));
		PaymentResponse actualResponse = service.getPaymentById(input);
		Assert.assertEquals(null, actualResponse.getRightsownerId());
		Assert.assertEquals("Number1", actualResponse.getRightsowner());
		Assert.assertEquals(3, actualResponse.getViewings().longValue());
		Assert.assertEquals(BigDecimal.valueOf(9L), actualResponse.getRoyalty());

	}

	@Test(expected = ValidationException.class)
	public void shouldPaymentByIdKO_whenNotFoundReference() {
		String input = "reference";
		Mockito.when(studioRepository.findByReference(input)).thenReturn(Optional.empty());
		service.getPaymentById(input);
	}


	@Test
	public void shouldAllPaymentOk_whenThereArePaymentsInDB() {
		StudioEntity studioEntity1 = new StudioEntity(1L, "refe1", "Number1", BigDecimal.valueOf(3), 3L);
		StudioEntity studioEntity2 = new StudioEntity(2L, "refe2", "Number2", BigDecimal.valueOf(2), 2L);
		Iterable<StudioEntity> iterable = Arrays.asList(studioEntity1, studioEntity2);
		Mockito.when(studioRepository.findAll()).thenReturn(iterable);
		List<PaymentResponse> actualResponse = service.getAllPayment();

		Assert.assertEquals(2, actualResponse.size());
		// First object
		Assert.assertEquals("refe1", actualResponse.get(0).getRightsownerId());
		Assert.assertEquals("Number1", actualResponse.get(0).getRightsowner());
		Assert.assertEquals(3, actualResponse.get(0).getViewings().longValue());
		Assert.assertEquals(BigDecimal.valueOf(9L), actualResponse.get(0).getRoyalty());
		// Second
		Assert.assertEquals("refe2", actualResponse.get(1).getRightsownerId());
		Assert.assertEquals("Number2", actualResponse.get(1).getRightsowner());
		Assert.assertEquals(2, actualResponse.get(1).getViewings().longValue());
		Assert.assertEquals(BigDecimal.valueOf(4L), actualResponse.get(1).getRoyalty());

	}

	@Test
	public void shouldAllPaymentOk_whenDBisEmpty() {
		Iterable<StudioEntity> iterable = Arrays.asList();
		Mockito.when(studioRepository.findAll()).thenReturn(iterable);
		List<PaymentResponse> actualResponse = service.getAllPayment();

		Assert.assertEquals(0, actualResponse.size());

	}
}
