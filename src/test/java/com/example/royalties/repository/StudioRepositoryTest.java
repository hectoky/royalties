package com.example.royalties.repository;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.royalties.RoyaltiesApplication;
import com.example.royalties.entity.StudioEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { RoyaltiesApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class StudioRepositoryTest {

	@Autowired
	private StudioRepository repository;

	@Test
	public void shouldUpdateSuccessFully_WhenTheDataExists() {
		String reference = "665115721c6f44e49be3bd3e26606026";
		Optional<StudioEntity> optional = repository.findByReference(reference);
		Long newValue = optional.get().getViews() + 4;
		repository.updateView(newValue, reference);
		optional = repository.findByReference(reference);
		Assert.assertEquals(newValue, optional.get().getViews());
	}

	@Test
	public void shouldNotThowException_WhenTheDataNotFoundDoingTheUpdate() {
		repository.updateView(12L, "NotFound");
	}

	@Test
	public void shouldFindSuccessFully_WhenTheDataExists() {
		String reference = "665115721c6f44e49be3bd3e26606026";
		Optional<StudioEntity> optional = repository.findByReference(reference);
		Assert.assertTrue(optional.isPresent());
	}

	@Test
	public void shouldRetreieveEmptyOptional_WhenNotFound() {
		String reference = "NotFound";
		Optional<StudioEntity> optional = repository.findByReference(reference);
		Assert.assertTrue(!optional.isPresent());
	}
}
