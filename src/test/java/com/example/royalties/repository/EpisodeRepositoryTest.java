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
import com.example.royalties.entity.EpisodeEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { RoyaltiesApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class EpisodeRepositoryTest {

	@Autowired
	private EpisodeRepository repository;


	@Test
	public void shouldFindSuccessFully_WhenTheDataExists() {
		String reference = "6a1db5d6610a4c048d3df9a6268c68dc";
		Optional<EpisodeEntity> optional = repository.findByReference(reference);
		Assert.assertTrue(optional.isPresent());
	}

	@Test
	public void shouldRetreieveEmptyOptional_WhenNotFound() {
		String reference = "NotFound";
		Optional<EpisodeEntity> optional = repository.findByReference(reference);
		Assert.assertTrue(!optional.isPresent());
	}
}
