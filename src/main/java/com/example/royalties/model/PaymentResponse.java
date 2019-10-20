package com.example.royalties.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PaymentResponse {
	@JsonInclude(Include.NON_NULL)
	String rightsownerId;
	String rightsowner;
	BigDecimal royalty;
	Long viewings;

}
