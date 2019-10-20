package com.example.royalties.service;

import java.util.List;

import com.example.royalties.model.PaymentResponse;

public interface PaymentService {

	public PaymentResponse getPaymentById(String studioReference);

	public List<PaymentResponse> getAllPayment();
}
