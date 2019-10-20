package com.example.royalties.service;

import javax.validation.ValidationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.royalties.RoyaltiesApplication;
import com.example.royalties.entity.StudioEntity;
import com.example.royalties.repository.StudioRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { RoyaltiesApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ViewServiceImplTest {

	@Autowired
	private StudioRepository studioRepository;

	@Autowired
	private ViewService service;

	@Test
	public void shouldViewOk_whenRefenreceIsFound() {
		String episodeReference = "6a1db5d6610a4c048d3df9a6268c68dc";
		String studioReference = "665115721c6f44e49be3bd3e26606026";
		StudioEntity entity = studioRepository.findByReference(studioReference).get();
		Long expectedNewValue = entity.getViews() + 1;
		service.view(episodeReference);
		Long actualValue = studioRepository.findByReference(studioReference).get().getViews();
		Assert.assertEquals(expectedNewValue, actualValue);

	}

	@Test(expected = ValidationException.class)
	public void shouldViewKO_whenRefenreceIsNotFound() {
		String episodeReference = "NotFound";
		service.view(episodeReference);

	}

	@Test
	public void shouldAllViewSet0_whenResetOK() {
		// Increase at least one view in all the owners
		service.view("6a1db5d6610a4c048d3df9a6268c68dc");
		service.view("6a1db5d6610a4c048d3df9a6268c68dc");
		service.view("78a7efb2bb36491996ff562f118d5a3d");
		service.view("dfde22b2a3f24401b12eeccc28cf1570");
		service.view("d5ca9218562a4c94bdca9955cec2870e");
		service.view("9159d302c3104e58a01fa397a3382b0d");
		// Check that all the owners have at least one view
		studioRepository.findAll().forEach(entity -> Assert.assertTrue(!entity.getViews().equals(0L)));
		
		service.reset();
		
		studioRepository.findAll().forEach(entity -> Assert.assertTrue(entity.getViews().equals(0L)));
	}

}
