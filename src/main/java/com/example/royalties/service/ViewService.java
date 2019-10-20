package com.example.royalties.service;

import javax.validation.ValidationException;

import com.example.royalties.model.ViewRequest;

public interface ViewService {

	public void validation(ViewRequest request) throws ValidationException;

	public void view(String episodeReference);

	public void reset();

}
