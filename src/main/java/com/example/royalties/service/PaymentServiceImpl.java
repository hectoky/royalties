package com.example.royalties.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.royalties.entity.StudioEntity;
import com.example.royalties.model.PaymentResponse;
import com.example.royalties.repository.StudioRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

	private StudioRepository studioRepository;

	@Autowired
	public PaymentServiceImpl(StudioRepository studioRepository) {
		this.studioRepository = studioRepository;
	}

	@Override
	public PaymentResponse getPaymentById(String studioReference) {
		Optional<StudioEntity> studioOptional = studioRepository.findByReference(studioReference);

		if (!studioOptional.isPresent())
			throw new ValidationException("Not found a resource for the studio" + studioReference);
		StudioEntity studio = studioOptional.get();
		return new PaymentResponse(null, studio.getName(),
				studio.getPayment().multiply(BigDecimal.valueOf(studio.getViews().longValue())), studio.getViews());
	}

	@Override
	public List<PaymentResponse> getAllPayment() {
		Iterable<StudioEntity> studioIterable = studioRepository.findAll();
		return StreamSupport.stream(studioIterable.spliterator(), false)
				.map(entity -> new PaymentResponse(entity.getReference(), entity.getName(),
						entity.getPayment().multiply(BigDecimal.valueOf(entity.getViews().longValue())),
						entity.getViews()))
				.collect(Collectors.toList());
	}

}
